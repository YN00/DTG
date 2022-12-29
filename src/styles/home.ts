import { Dimensions, StyleSheet } from 'react-native';

const dimension = Dimensions.get('screen');

export const homeStyles = StyleSheet.create({
  safeAreaView: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: '#05031f',
  },
  container: {
    flex: 1,
    flexDirection: 'column',
    backgroundColor: '#fff',
  },
  titleText: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#000',
  },
  topView: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'space-between',
    height: 230,
    paddingTop: 30,
    paddingBottom: 30,
    paddingLeft: 25,
    paddingRight: 25,
  },
  statusTitleText: {
    paddingTop: 25,
    paddingBottom: 10,
  },
  statusText: {
    color: '#000',
    paddingTop: 10,
  },
  statusIconAndTextView: {
    flexDirection: 'column',
    alignItems: 'center',
    backgroundColor: '#fff',
  },
  statusMenuView: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
  },
  middleAreaView: {
    flex: 1,
    paddingTop: 80,
    paddingLeft: 20,
    paddingRight: 20,
    paddingBottom: 20,
    // backgroundColor: '#fff',
    borderColor: '#000',
    borderStyle: 'solid',
  },
  currentStatusView: {
    padding: 20,
    position: 'absolute',
    zIndex: 2,
    top: -130,
    width: dimension.width - 40,
    height: dimension.height / 4.8,
    borderRadius: 20,
    backgroundColor: '#fff',
    borderStyle: 'solid',
    borderBottomColor: 'gray',
    elevation: 10,
  },
  etasView: {
    flex: 1,
    justifyContent: 'flex-start',
    paddingTop: 20,
    paddingBottom: 20,
    paddingLeft: 25,
    paddingRight: 25,
    backgroundColor: '#d6341e',
    borderRadius: 20,
  },
  dailyView: {
    backgroundColor: '#fff',
    padding: 20,
  },
  dailyContentView: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: 10,
  },
  dailyContentIconAndText: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  dailyAreaIcons: {
    backgroundColor: '#edeff0',
  },
  totalDriveDistanceView: {
    flex: 3,
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 20,
    position: 'relative',
    bottom: 0,
    backgroundColor: '#edeff0',
  },
  iconStyle: {
    padding: 10,
    borderRadius: 50,
  },
  dataAndBluetoothIcon: {
    backgroundColor: '#18d9cc',
  },
  gpsIcon: {
    backgroundColor: '#c4340c',
  },
});
