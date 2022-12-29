import React from 'react';
import { TouchableOpacity } from 'react-native';
import {
  createBottomTabNavigator,
  BottomTabHeaderProps,
} from '@react-navigation/bottom-tabs';

import Home from './Home';
import DrivingRecord from './DrivingRecord';
import EditInfo from './EditInfo';
import Setting from './Setting';
import MainBottomTabHeader from './MainBottomTabHeader';

import Ionicons from 'react-native-vector-icons/Ionicons';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import FontAwesome5Icon from 'react-native-vector-icons/FontAwesome5';

/**
 * Tab에 포함되는 화면 정의
 */
export type MainBottomTabParamList = {
  Home: undefined;
  DrivingRecord: undefined;
  EditInfo: undefined;
  Setting: undefined;
};

const MainBottomTab = createBottomTabNavigator<MainBottomTabParamList>();

const MainBottomTabs = () => {
  return (
    <MainBottomTab.Navigator
      initialRouteName="Home"
      screenOptions={{
        header: (props: BottomTabHeaderProps) => (
          <MainBottomTabHeader {...props} />
        ),
        tabBarButton: (props) => <TouchableOpacity {...props} />,
        tabBarActiveTintColor: '#000',
        tabBarStyle: {
          backgroundColor: 'white',
          paddingTop: 5,
        },
      }}
    >
      <MainBottomTab.Screen
        name="Home"
        component={Home}
        options={{
          tabBarActiveTintColor: '#000',
          headerShown: false,
          tabBarIcon: ({ color }) => (
            <MaterialCommunityIcons name="home" color={color} size={25} />
          ),
        }}
      />
      <MainBottomTab.Screen
        name="DrivingRecord"
        component={DrivingRecord}
        options={{
          title: '운행기록',
          tabBarIcon: ({ color }) => (
            <Ionicons name="document-text" color={color} size={25} />
          ),
        }}
      />
      <MainBottomTab.Screen
        name="EditInfo"
        component={EditInfo}
        options={{
          title: '정보수정',
          tabBarIcon: ({ color }) => (
            <FontAwesome5Icon name="car" color={color} size={25} />
          ),
        }}
      />
      <MainBottomTab.Screen
        name="Setting"
        component={Setting}
        options={{
          title: '설정',
          tabBarIcon: ({ color }) => (
            <MaterialIcons name="settings" color={color} size={25} />
          ),
        }}
      />
    </MainBottomTab.Navigator>
  );
};

export default MainBottomTabs;
