export {};

const mockAndroidCommands = { create: jest.fn(), setParams: jest.fn() };
const mockDispatchViewManagerCommand = jest.fn();
const mockPlatform = { OS: 'android' };
const mockViewId = 7;

jest.mock('react', () => ({
  ...jest.requireActual('react'),
  useEffect: (effect: () => void) => effect(),
  useRef: (initial: unknown) => ({ current: initial }),
  useState: (initial: unknown) => [initial, jest.fn()],
}));

jest.mock('react-native', () => ({
  DeviceEventEmitter: { addListener: () => ({ remove: jest.fn() }) },
  Platform: mockPlatform,
  UIManager: {
    getViewManagerConfig: () => ({ Commands: { create: 1, setParams: 2 } }),
    dispatchViewManagerCommand: mockDispatchViewManagerCommand,
  },
  findNodeHandle: () => mockViewId,
}));

jest.mock('react-native/Libraries/Utilities/codegenNativeCommands', () => ({
  __esModule: true,
  default: () => mockAndroidCommands,
}));

const { useSmileIDView } = require('../useSmileIDView');

const onResult = jest.fn();

beforeEach(() => {
  jest.clearAllMocks();
});

describe('useSmileIDView on Android', () => {
  beforeEach(() => {
    mockPlatform.OS = 'android';
  });

  it('sends create and setParams through the native command dispatcher', () => {
    useSmileIDView('SmileIDDocumentCaptureView', {
      userId: 'user-1',
      onResult,
    });

    expect(mockAndroidCommands.create).toHaveBeenCalledWith(null, mockViewId);
    expect(mockAndroidCommands.setParams).toHaveBeenCalledTimes(1);
    expect(mockDispatchViewManagerCommand).not.toHaveBeenCalled();
  });

  it('leaves function props such as onResult out of setParams', () => {
    useSmileIDView('SmileIDDocumentCaptureView', {
      userId: 'user-1',
      onResult,
    });

    const [, viewId, params] = mockAndroidCommands.setParams.mock.calls[0];
    expect(viewId).toBe(mockViewId);
    expect(params).toEqual({ userId: 'user-1' });
  });
});

describe('useSmileIDView on iOS', () => {
  beforeEach(() => {
    mockPlatform.OS = 'ios';
  });

  it('keeps dispatching through UIManager', () => {
    useSmileIDView('SmileIDDocumentCaptureView', { userId: 'user-1' });

    expect(mockDispatchViewManagerCommand).toHaveBeenCalledWith(mockViewId, 1, [
      mockViewId,
    ]);
    expect(mockDispatchViewManagerCommand).toHaveBeenCalledWith(mockViewId, 2, [
      mockViewId,
      { userId: 'user-1' },
    ]);
    expect(mockAndroidCommands.create).not.toHaveBeenCalled();
  });
});
