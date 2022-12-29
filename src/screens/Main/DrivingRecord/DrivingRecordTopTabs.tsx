import React from 'react';
import { useWindowDimensions } from 'react-native';
import { createMaterialTopTabNavigator } from '@react-navigation/material-top-tabs';

import AllRecordTab from './Tabs/AllRecordTab';
import FailedRecordTab from './Tabs/FailedRecordTab';

export type DrivingRecordTopTabParamList = {
  AllRecord: undefined;
  FailedRecord: undefined;
};

const Tab = createMaterialTopTabNavigator<DrivingRecordTopTabParamList>();

const DrivingRecordTopTabs = () => {
  const layout = useWindowDimensions();

  return (
    <Tab.Navigator
      initialRouteName="AllRecord"
      initialLayout={{ width: layout.width }}
    >
      <Tab.Screen
        name="AllRecord"
        component={AllRecordTab}
        options={{
          tabBarLabel: '전체이력',
          tabBarIndicatorStyle: {},
          tabBarActiveTintColor: '#000',
          tabBarPressColor: 'transparent',
        }}
      />
      <Tab.Screen
        name="FailedRecord"
        component={FailedRecordTab}
        options={{
          tabBarLabel: '실패이력',
          tabBarIndicatorStyle: {},
          tabBarActiveTintColor: '#000',
          tabBarPressColor: 'transparent',
        }}
      />
    </Tab.Navigator>
  );
};

export default DrivingRecordTopTabs;
