import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import DeviceConnection from './src/screens/DeviceConnection';
import TermsUse from './src/screens/TermsUse';
import VehicleRegistration from './src/screens/Main/DrivingRecord/VehicleRegistration';
import DriverRegistration from './src/screens/DriverRegistration';
import CompanyRegistration from './src/screens/CompanyRegistration';
import Main from './src/screens/Main';

/**
 * Root Stack에 포함되는 화면 정의
 */
export type RootStackParamList = {
  DeviceConnection: undefined;
  TermsUse: undefined;
  VehicleRegistration: undefined;
  DriverRegistration: undefined;
  CompanyRegistration: undefined;
  Main: undefined;
};

const RootStack = createNativeStackNavigator<RootStackParamList>();

const RootStackScreens = () => {
  // 장치 블루투스 연결 여부
  const isDeviceConnection = true;

  return (
    <RootStack.Navigator>
      {!isDeviceConnection ? (
        <>
          <RootStack.Screen
            name="DeviceConnection"
            component={DeviceConnection}
            options={{ title: '기기연결' }}
          />
          <RootStack.Screen
            name="TermsUse"
            component={TermsUse}
            options={{ title: '약관 동의' }}
          />
          <RootStack.Screen
            name="VehicleRegistration"
            component={VehicleRegistration}
            options={{ title: '차량정보 등록(1/3)' }}
          />
          <RootStack.Screen
            name="DriverRegistration"
            component={DriverRegistration}
            options={{ title: '운전자정보 등록(2/3)' }}
          />
          <RootStack.Screen
            name="CompanyRegistration"
            component={CompanyRegistration}
            options={{ title: '회사정보 등록(3/3)' }}
          />
        </>
      ) : (
        <RootStack.Screen
          name="Main"
          component={Main}
          options={{
            headerShown: false,
          }}
        />
      )}
    </RootStack.Navigator>
  );
};

const AppInner = () => {
  return (
    <NavigationContainer>
      <RootStackScreens />
    </NavigationContainer>
  );
};

export default AppInner;
