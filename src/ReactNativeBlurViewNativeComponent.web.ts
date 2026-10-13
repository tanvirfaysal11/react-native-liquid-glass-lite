// Web counterpart of the codegen spec module. The native module calls
// codegenNativeComponent() at load time, which cannot run under
// react-native-web — Metro resolves this file instead on web, keeping the raw
// export importable there. The platform suffix also excludes it from codegen.
export { default } from './BlurView.web';
export type { BlurType } from './ReactNativeBlurViewNativeComponent';
