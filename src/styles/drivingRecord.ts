import { StyleSheet } from 'react-native';

export const recordStyles = StyleSheet.create({
  safeAreaView: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: '#05031f',
  },
  container: {
    flex: 1,
  },
  summaryTopView: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 10,
  },
  successRateView: {
    paddingBottom: 25,
    flexDirection: 'row',
  },
  successRateText: {
    fontSize: 25,
    color: '#fff',
  },
  successRatePercent: {
    fontSize: 25,
    color: '#17e8e1',
  },
  recentlyText: {
    color: '#fff',
    paddingBottom: 10,
  },
  successColor: {
    color: '#17e8e1',
  },
  failedColor: {
    color: '#e82c1e',
  },
  historyDetails: {
    flex: 3,
  },
});
