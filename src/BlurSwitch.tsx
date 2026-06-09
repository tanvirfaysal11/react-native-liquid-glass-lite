import React from 'react';
import { Platform, StyleSheet, Switch } from 'react-native';
import type { ColorValue, StyleProp, ViewStyle } from 'react-native';
import ReactNativeBlurSwitch from './ReactNativeBlurSwitchNativeComponent';

/**
 * Converts a `ColorValue` to a plain hex/named string.
 * Falls back to `fallback` when the value is a platform-specific color object.
 */
function toColorString(color: ColorValue | undefined, fallback: string): string {
  return typeof color === 'string' ? color : fallback;
}

export interface BlurSwitchProps {
  /** Controlled value. @default false */
  value?: boolean;
  /** Called when the user toggles the switch. */
  onValueChange?: (value: boolean) => void;
  /** Blur intensity of the switch track (Android). @default 10 */
  blurAmount?: number;
  /** Number of blur passes for smoother blur (Android). @default 5 */
  blurRounds?: number;
  /** Color of the switch thumb. @default '#FFFFFF' */
  thumbColor?: ColorValue;
  /** Track color for on/off states. */
  trackColor?: { false?: ColorValue; true?: ColorValue };
  /** Disables the switch. @default false */
  disabled?: boolean;
  style?: StyleProp<ViewStyle>;
}

/**
 * A toggle switch that uses a native blur effect on the track (Android).
 *
 * On iOS the component delegates to the built-in `Switch` component because
 * iOS already renders the system switch with native material effects.
 *
 * @example
 * ```tsx
 * const [enabled, setEnabled] = React.useState(false);
 *
 * <BlurSwitch
 *   value={enabled}
 *   onValueChange={setEnabled}
 *   trackColor={{ false: '#E5E5EA', true: '#34C759' }}
 * />
 * ```
 */
export const BlurSwitch: React.FC<BlurSwitchProps> = ({
  value = false,
  blurAmount = 10,
  blurRounds = 5,
  onValueChange,
  thumbColor = '#FFFFFF',
  trackColor = { false: '#E5E5EA', true: '#34C759' },
  disabled = false,
  style,
  ...rest
}) => {
  // iOS already has native material effects – delegate to the system Switch.
  if (Platform.OS === 'ios') {
    return (
      <Switch
        value={value}
        onValueChange={onValueChange}
        thumbColor={toColorString(thumbColor, '#FFFFFF')}
        trackColor={{
          false: toColorString(trackColor?.false, '#E5E5EA'),
          true: toColorString(trackColor?.true, '#34C759'),
        }}
        disabled={disabled}
        style={style}
        {...rest}
      />
    );
  }

  return (
    <ReactNativeBlurSwitch
      style={[styles.switch, style]}
      value={value}
      onValueChange={(event) => onValueChange?.(event.nativeEvent.value)}
      blurAmount={blurAmount}
      blurRounds={blurRounds}
      thumbColor={toColorString(thumbColor, '#FFFFFF')}
      trackColorOff={toColorString(trackColor?.false, '#E5E5EA')}
      trackColorOn={toColorString(trackColor?.true, '#34C759')}
      disabled={disabled}
      {...rest}
    />
  );
};

export default BlurSwitch;

const styles = StyleSheet.create({
  switch: {
    width: 65,
    height: 36,
  },
});
