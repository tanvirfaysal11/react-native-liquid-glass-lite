// Web implementation of BlurSwitch.
import React from 'react';
import { Switch } from 'react-native';
import type { BlurSwitchProps } from './BlurSwitch';

/**
 * Web implementation of BlurSwitch.
 *
 * The blur-styled switch is an Android-native component; on iOS the wrapper
 * already renders React Native's built-in Switch, and the web version mirrors
 * that behaviour. `blurAmount` and `blurRounds` are native-only and accepted
 * for API compatibility.
 */
export const BlurSwitch: React.FC<BlurSwitchProps> = ({
  value = false,
  onValueChange,
  thumbColor = '#FFFFFF',
  trackColor = { false: '#E5E5EA', true: '#34C759' },
  disabled = false,
  style,
  blurAmount: _blurAmount,
  blurRounds: _blurRounds,
  ...rest
}) => {
  return (
    <Switch
      value={value}
      onValueChange={onValueChange}
      thumbColor={thumbColor}
      trackColor={trackColor}
      disabled={disabled}
      style={style}
      {...rest}
    />
  );
};

export default BlurSwitch;
