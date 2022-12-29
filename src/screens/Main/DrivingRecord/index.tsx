import React from 'react';

import { SafeAreaView, View, Text } from 'react-native';
import DrivingRecordTopTabs from './DrivingRecordTopTabs';
import { recordStyles } from '../../../styles/drivingRecord';

const DrivingRecord = (): JSX.Element => {
  return (
    <SafeAreaView style={recordStyles.safeAreaView}>
      <View style={recordStyles.container}>
        <View style={recordStyles.summaryTopView}>
          <View style={recordStyles.successRateView}>
            <Text style={recordStyles.successRateText}>성공률 </Text>
            <Text style={recordStyles.successRatePercent}>98.5%</Text>
          </View>
          <Text style={recordStyles.recentlyText}>최근 1주일 간</Text>
          <Text style={{ color: '#fff' }}>성공 1,000건 실패 15건</Text>
        </View>
        <View style={recordStyles.historyDetails}>
          <DrivingRecordTopTabs />
        </View>
      </View>
    </SafeAreaView>
  );
};

export default DrivingRecord;
