/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */
import 'react-native-gesture-handler';
import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';

import { LogInComponent } from './components/log-in';
import { RegisterComponent } from './components/register';
import { SelectCityComponent } from './components/user-logged-components/select-city';
import { EventListComponent } from './components/user-logged-components/event/event-list';
import { UserOwnedEventsComponent } from './components/user-logged-components/event/owned-events-list';
import { EventDetailsComponent } from './components/user-logged-components/event/event-details';
import { NewEventComponent } from './components/user-logged-components/event/event-new';

const Stack = createStackNavigator();

export default function App () {

  return (
    <>
      <StatusBar barStyle="dark-content" />
    
      <NavigationContainer>
        <Stack.Navigator>
          <Stack.Screen
            name="LogIn"
            component={LogInComponent}
            options={{headerShown: false}}/>
           <Stack.Screen
            name="Register"
            component={RegisterComponent}
            options={{headerShown: false}}
          />
          <Stack.Screen
             name="SelectCity"
             component={SelectCityComponent}
             options={{headerShown: false}}
          />
         <Stack.Screen
             name="EventListCity"
             component={EventListComponent}
             options={{headerShown: false}}
          />
          <Stack.Screen
             name="OwnedEvents"
             component={UserOwnedEventsComponent}
             options={{headerShown: false}}
          />
          <Stack.Screen
             name="EventDetails"
             component={EventDetailsComponent}
          />
          <Stack.Screen
             name="NewEvent"
             component={NewEventComponent}
             options={{headerShown: false}}
          />
        </Stack.Navigator> 
      </NavigationContainer>
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

