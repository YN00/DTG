import React from 'react';
import { View, ScrollView, SafeAreaView, Text, Linking } from 'react-native';
import { Button } from '@rneui/themed';

import FontIsto from 'react-native-vector-icons/Fontisto';
import Ionicons from 'react-native-vector-icons/Ionicons';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import FontAwesome5Icon from 'react-native-vector-icons/FontAwesome5';
import { homeStyles } from '../../../styles/home';

const TopArea = () => {
  return (
    <View style={homeStyles.topView}>
      <View style={{ justifyContent: 'space-between' }}>
        <Text style={{ fontSize: 20 }}>
          <Text style={{ color: '#fff' }}>aid </Text>
          <Text style={{ color: '#18d9cc' }}>DTG</Text>
        </Text>
      </View>
      <Text style={{ fontSize: 16 }}>
        <MaterialIcons
          name="gps-fixed"
          color="white"
          size={12}
          style={{ paddingRight: 25 }}
        />
        <Text style={{ color: '#fff' }}> 서울시 영등포구 양평동</Text>
      </Text>
    </View>
  );
};

const CardArea = () => {
  return (
    <View style={{ alignItems: 'center' }}>
      <View style={homeStyles.currentStatusView}>
        <Text style={{ color: '#000', fontSize: 30, fontWeight: 'bold' }}>
          서울12가 1234
        </Text>
        <Text style={{ color: '#000', fontSize: 20, paddingTop: 25 }}>
          현재 주행속도
        </Text>
        <View
          style={{
            justifyContent: 'flex-start',
            alignItems: 'flex-end',
            flexDirection: 'row',
          }}
        >
          <Text style={{ color: '#000', fontSize: 40 }}>123 </Text>
          <Text style={{ color: '#000', fontSize: 20, lineHeight: 38 }}>
            km/h
          </Text>
        </View>
      </View>
    </View>
  );
};

const ETasArea = () => {
  return (
    <Button
      type="solid"
      buttonStyle={homeStyles.etasView}
      onPress={async () => {
        await Linking.openURL('https://www.naver.com');
      }}
    >
      eTas 종합진단표 바로가기
    </Button>
  );
};

const StatusArea = () => {
  return (
    <>
      <View style={homeStyles.statusTitleText}>
        <Text style={homeStyles.titleText}>연결상태</Text>
      </View>
      <View style={homeStyles.statusMenuView}>
        <View style={homeStyles.statusIconAndTextView}>
          <FontIsto
            name="cloud-up"
            color="white"
            size={25}
            style={[homeStyles.iconStyle, homeStyles.dataAndBluetoothIcon]}
          />
          <Text style={homeStyles.statusText}>데이터 전송</Text>
        </View>
        <View style={homeStyles.statusIconAndTextView}>
          <Ionicons
            name="bluetooth"
            color="white"
            size={25}
            style={[homeStyles.iconStyle, homeStyles.dataAndBluetoothIcon]}
          />
          <Text style={homeStyles.statusText}>기기연결</Text>
        </View>
        <View style={homeStyles.statusIconAndTextView}>
          <MaterialIcons
            name="gps-fixed"
            color="white"
            size={25}
            style={[homeStyles.iconStyle, homeStyles.gpsIcon]}
          />
          <Text style={homeStyles.statusText}>기기 GPS</Text>
        </View>
      </View>
    </>
  );
};

const DailyDriveInfo = () => {
  return (
    <View style={homeStyles.dailyView}>
      <Text style={homeStyles.titleText}>일일주행정보</Text>
      <View style={{ paddingTop: 15 }}>
        <View style={homeStyles.dailyContentView}>
          <View style={homeStyles.dailyContentIconAndText}>
            <FontAwesome5Icon
              name="road"
              color="black"
              size={20}
              style={[homeStyles.iconStyle, homeStyles.dailyAreaIcons]}
            />
            <Text style={{ color: '#000' }}> 주행거리</Text>
          </View>
          <Text style={{ color: '#000', fontWeight: 'bold' }}>1,248 Km</Text>
        </View>
        <View style={homeStyles.dailyContentView}>
          <View style={homeStyles.dailyContentIconAndText}>
            <FontAwesome5Icon
              name="clock"
              color="black"
              size={20}
              style={[homeStyles.iconStyle, homeStyles.dailyAreaIcons]}
            />
            <Text style={{ color: '#000' }}> 주행시간</Text>
          </View>
          <Text style={{ color: '#000', fontWeight: 'bold' }}>8시간 36분</Text>
        </View>
      </View>
    </View>
  );
};

const TotalDriveDistance = () => {
  return (
    <View style={homeStyles.totalDriveDistanceView}>
      <Text style={homeStyles.titleText}>누적주행거리</Text>
      <Text style={homeStyles.titleText}>145,248 km</Text>
    </View>
  );
};

const Home = (): JSX.Element => {
  // const isDarkMode = useColorScheme() === 'dark';

  return (
    <SafeAreaView style={homeStyles.safeAreaView}>
      <ScrollView
        overScrollMode="never"
        scrollToOverflowEnabled={false}
        automaticallyAdjustsScrollIndicatorInsets={false}
        bounces={true}
      >
        <TopArea />

        <View style={homeStyles.container}>
          <CardArea />

          <View style={homeStyles.middleAreaView}>
            <ETasArea />
            <StatusArea />
          </View>

          <View style={{ height: 5, backgroundColor: '#edeff0' }} />

          <DailyDriveInfo />
          <TotalDriveDistance />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default Home;
