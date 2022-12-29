import { BottomTabHeaderProps } from '@react-navigation/bottom-tabs';
import React from 'react';
import { Platform, SafeAreaView, StyleSheet, Text, View } from 'react-native';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';

interface HomeHeaderProps {
  isHome: boolean;
  title: string;
  isIos: boolean;
}

const HomeHeader = ({ isHome, title, isIos }: HomeHeaderProps) => {
  return isHome ? (
    <>
      <View
        style={isIos ? styles.homeHeaderView : styles.homeHeaderViewAndroid}
      >
        <View>
          <Text style={styles.homeHeaderText}>
            <Text style={styles.homeHeaderAid}>aid </Text>
            <Text style={styles.homeHeaderDTG}>DTG</Text>
          </Text>
        </View>
        <Text style={styles.locationText}>
          <MaterialIcons
            name="gps-fixed"
            color="white"
            size={12}
            style={styles.locationIcon}
          />
          <Text> 서울시 영등포구 양평동</Text>
        </Text>
      </View>
    </>
  ) : (
    <View style={isIos ? styles.iosHeaderView : styles.androidHeaderView}>
      <Text style={isIos ? styles.iosHeaderText : styles.androidHeaderText}>
        {title}
      </Text>
    </View>
  );
};

const MainBottomTabHeader = (props: BottomTabHeaderProps) => {
  const { options, route } = props;
  const isIos = Platform.OS === 'ios';

  const homeHeaderProps: HomeHeaderProps = {
    isHome: route.key.includes('Home'),
    title: options.title!,
    isIos,
  };

  return !isIos ? (
    <HomeHeader {...homeHeaderProps} />
  ) : (
    <SafeAreaView style={styles.iosHeaderSafeAreaView}>
      <HomeHeader {...homeHeaderProps} />
    </SafeAreaView>
  );
};

export default MainBottomTabHeader;

const styles = StyleSheet.create({
  androidHeaderView: {
    backgroundColor: '#05031f',
    height: 60,
    padding: 20,
  },
  androidHeaderText: {
    color: '#fff',
    textAlign: 'center',
    fontSize: 20,
    lineHeight: 23,
  },
  iosHeaderSafeAreaView: {
    backgroundColor: '#05031f',
  },
  iosHeaderView: {
    height: 50,
    paddingTop: 5,
  },
  iosHeaderText: {
    textAlign: 'center',
    fontSize: 20,
    color: '#fff',
  },
  homeHeaderView: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingLeft: 20,
    paddingRight: 20,
    paddingBottom: 20,
  },
  homeHeaderViewAndroid: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingLeft: 20,
    paddingRight: 20,
    paddingTop: 20,
    paddingBottom: 25,
    backgroundColor: '#05031f',
  },
  homeHeaderText: {
    fontSize: 20,
  },
  homeHeaderAid: {
    color: '#fff',
  },
  homeHeaderDTG: {
    color: '#18d9cc',
  },
  locationText: {
    fontSize: 16,
    color: '#fff',
  },
  locationIcon: {
    padding: 25,
  },
});
