import { useEffect, useRef, useState } from 'react';
import {
  DeviceEventEmitter,
  Platform,
  UIManager,
  findNodeHandle,
} from 'react-native';
import codegenNativeCommands from 'react-native/Libraries/Utilities/codegenNativeCommands';

interface SmileIDProps {
  onResult?: (event: { nativeEvent: { error: any; result: any } }) => void;
  [key: string]: any;
}

interface SmileIDNativeCommands {
  create: (viewRef: any, viewId: number | null) => void;
  setParams: (
    viewRef: any,
    viewId: number | null,
    params: Record<string, unknown>
  ) => void;
}

// On the New Architecture UIManager.dispatchViewManagerCommand is a no-op, so
// Android commands go through codegenNativeCommands, which works on both.
const AndroidCommands = codegenNativeCommands<SmileIDNativeCommands>({
  supportedCommands: ['create', 'setParams'],
});

const withoutFunctions = (props: SmileIDProps) =>
  Object.fromEntries(
    Object.entries(props).filter(([, value]) => typeof value !== 'function')
  );

export const useSmileIDView = (viewName: string, props: SmileIDProps) => {
  const viewRef = useRef<any>(null);
  const [viewProps, setViewProps] = useState<SmileIDProps>(props);
  const onResultRef = useRef(viewProps.onResult);
  useEffect(() => {
    onResultRef.current = viewProps.onResult;
  }, [viewProps.onResult]);

  useEffect(() => {
    const eventListener = DeviceEventEmitter.addListener(
      'onSmileResult',
      (event) => {
        // Use the ref to access the latest callback
        setViewProps((prev) => ({
          ...prev,
          _invalidationKey: Date.now(),
        }));
        if (onResultRef.current) {
          const nativeEvent = {
            nativeEvent: {
              error: event.error,
              result: event.result,
            },
          };
          onResultRef.current(nativeEvent);
        }
      }
    );

    return () => {
      eventListener.remove();
    };
  }, []);

  useEffect(() => {
    const viewId = findNodeHandle(viewRef.current);
    if (Platform.OS === 'android') {
      AndroidCommands.create(viewRef.current, viewId);
      return;
    }
    const commandId = UIManager.getViewManagerConfig(viewName).Commands.create;

    // Ensure the commandId is defined and is a number
    if (typeof commandId !== 'undefined') {
      UIManager.dispatchViewManagerCommand(
        findNodeHandle(viewRef.current),
        commandId,
        [viewId]
      );
    } else {
      throw new Error('Command "create" is not defined for MyNativeComponent');
    }
  }, [viewName]);

  useEffect(() => {
    const parameters = {
      ...viewProps,
    };

    const viewId = findNodeHandle(viewRef.current);
    if (Platform.OS === 'android') {
      AndroidCommands.setParams(
        viewRef.current,
        viewId,
        withoutFunctions(parameters)
      );
      return;
    }
    const commandId =
      UIManager.getViewManagerConfig(viewName).Commands.setParams;

    // Ensure the commandId is defined and is a number
    if (typeof commandId !== 'undefined') {
      UIManager.dispatchViewManagerCommand(
        findNodeHandle(viewRef.current),
        commandId,
        [viewId, parameters]
      );
    } else {
      throw new Error(
        'Command "setParams" is not defined for MyNativeComponent'
      );
    }
  }, [viewProps, viewName]);

  return viewRef;
};
