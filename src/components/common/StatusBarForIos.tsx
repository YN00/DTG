import React from 'react';
import { View, StatusBar, Platform } from 'react-native';

const StatusBarForIos = () => {
  const isIos = Platform.OS === 'ios';

  return isIos ? (
    <View
      style={{ backgroundColor: '#05031f', height: StatusBar.currentHeight }}
    >
      <StatusBar translucent barStyle="light-content" />
    </View>
  ) : (
    <></>
  );
};

export default StatusBarForIos;
