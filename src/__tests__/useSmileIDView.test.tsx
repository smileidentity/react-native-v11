type Effect = { deps?: any[]; cleanup?: void | (() => void) };

const mockListeners: Record<string, Array<(event: any) => void>> = {};
const mockDispatch = jest.fn();

jest.mock('react-native', () => ({
  DeviceEventEmitter: {
    addListener: (name: string, listener: (event: any) => void) => {
      (mockListeners[name] ||= []).push(listener);
      return {
        remove: () => {
          mockListeners[name] = mockListeners[name]!.filter(
            (existing) => existing !== listener
          );
        },
      };
    },
  },
  Platform: { OS: 'ios' },
  UIManager: {
    getViewManagerConfig: () => ({ Commands: { create: 1, setParams: 2 } }),
    dispatchViewManagerCommand: (...args: any[]) => mockDispatch(...args),
  },
  findNodeHandle: () => 42,
}));

// Minimal hook runtime: react-test-renderer is not a dependency of this package.
const mockSlots: any[] = [];
let mockSlotIndex = 0;
let mockRerender: () => void = () => {};

jest.mock('react', () => ({
  useRef: (initial: any) => {
    const index = mockSlotIndex++;
    mockSlots[index] ||= { current: initial };
    return mockSlots[index];
  },
  useState: (initial: any) => {
    const index = mockSlotIndex++;
    mockSlots[index] ||= { value: initial };
    const slot = mockSlots[index];
    const setValue = (next: any) => {
      slot.value = typeof next === 'function' ? next(slot.value) : next;
      mockRerender();
    };
    return [slot.value, setValue];
  },
  useEffect: (run: () => void | (() => void), deps?: any[]) => {
    const index = mockSlotIndex++;
    const previous: Effect | undefined = mockSlots[index];
    const changed =
      !previous ||
      !deps ||
      !previous.deps ||
      deps.some((dep, depIndex) => dep !== previous.deps![depIndex]);
    if (changed) {
      mockPendingEffects.push(() => {
        if (previous && typeof previous.cleanup === 'function') {
          previous.cleanup();
        }
        mockSlots[index] = { deps, cleanup: run() };
      });
    }
  },
}));

const mockPendingEffects: Array<() => void> = [];

import { useSmileIDView } from '../useSmileIDView';

const render = (props: any) => {
  const run = () => {
    mockSlotIndex = 0;
    // eslint-disable-next-line react-hooks/rules-of-hooks
    useSmileIDView('SmileIDSmartSelfieCaptureView', props);
    mockPendingEffects.splice(0).forEach((effect) => effect());
  };
  mockRerender = run;
  run();
};

const emit = (event: any) =>
  (mockListeners.onSmileResult ?? []).slice().forEach((l) => l(event));

const setParamsCalls = () =>
  mockDispatch.mock.calls.filter(([, command]) => command === 2);

const resultWithLivenessFiles = (count: number) =>
  JSON.stringify({
    selfieFile: '/tmp/selfie.jpg',
    livenessFiles: Array.from({ length: count }, (_, i) => `/tmp/l${i}.jpg`),
  });

describe('useSmileIDView', () => {
  beforeEach(() => {
    mockSlots.length = 0;
    mockPendingEffects.length = 0;
    Object.keys(mockListeners).forEach((key) => delete mockListeners[key]);
    mockDispatch.mockClear();
  });

  it.each([6, 7, 12])(
    'passes a result with %i liveness files through unchanged',
    (count) => {
      const onResult = jest.fn();
      render({ onResult, userId: 'user' });

      const payload = resultWithLivenessFiles(count);
      emit({ result: payload });

      expect(onResult).toHaveBeenCalledTimes(1);
      const delivered = onResult.mock.calls[0][0].nativeEvent.result;
      expect(delivered).toBe(payload);
      expect(JSON.parse(delivered).livenessFiles).toHaveLength(count);
    }
  );

  it('passes errors through', () => {
    const onResult = jest.fn();
    render({ onResult });
    emit({ error: 'boom' });
    expect(onResult.mock.calls[0][0].nativeEvent.error).toBe('boom');
  });

  it('re-dispatches setParams once after a result so the view can start a new capture', () => {
    render({ onResult: jest.fn(), userId: 'user' });
    expect(setParamsCalls()).toHaveLength(1);

    emit({ result: resultWithLivenessFiles(7) });

    expect(setParamsCalls()).toHaveLength(2);
  });
});
