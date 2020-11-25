const { exp } = require("react-native/Libraries/Animated/src/Easing");
import React from 'react';
import { Image, StyleSheet, Text, View, TextInput,  TouchableOpacity } from 'react-native';
import { EventService } from '../../service/event.service';

export class SelectCityComponent extends React.Component{
    state = {
        loggedUser: this.props.route.params.user,
        searchKey: "all",
        hasResults: true
    }
    constructor(props){
        super(props);
    }

    handleCitySelected(){
        this.props.navigation.navigate("EventListCity",{username: this.state.loggedUser,
             city: this.state.searchKey})
    }

    textChangedEvent(text){
        let evService = new EventService()
        this.setState({
            searchKey:text,
            hasResults: evService.eventsFromCity(text).length > 0
        })
    }
   
    render(){
        return (
            <>
                <View style={styles.screenContainer}>
                     <Text style={styles.welcomeMsg}>Welcome, @{this.state.loggedUser}!</Text>
                     <Text style={styles.infoText}> 
                     Just one more thing, enter the city where you want to see events, if we have events there an arrow will appear :) 
                     </Text>
                     <View style={styles.inputView} >
                            <TextInput  
                                style={styles.inputText}
                                placeholder="city" 
                                placeholderTextColor="#003f5c"
                                onChangeText={text=>{this.textChangedEvent(text)}}/>
                     </View>
                     {this.state.hasResults &&
                        <TouchableOpacity onPress={()=>{this.handleCitySelected()}}>
                            <Image
                                style={styles.arrow}
                                source={require('./right-arrow.png')}
                            />
                       </TouchableOpacity>
                    }
                     <Image
                        source={require('./ferriswh.png')} 
                        style={styles.image}/>
                </View>
            </>
        )
    }
}


const styles = StyleSheet.create({
    screenContainer: {
        flex: 1,
        backgroundColor: '#B1CCEB',
        alignItems: 'center',
        paddingTop: "25%"
    },
    welcomeMsg: {
        fontSize:25,
        marginBottom:40
    },
    infoText:{
        fontSize: 20,
        textAlign: "center",
        height: "auto",
        marginLeft: "10%",
        marginRight: "10%"
    },
    image:{
        height: "50%",
        width: "100%",
        marginTop: "10%",
        bottom: 0, 
    },
    inputView:{
        width:"80%",
        height:50,
        marginTop: "2%",
        marginBottom:20,
        justifyContent:"center",
        padding:20,
        borderBottomWidth: 1,
        borderBottomColor: "black"
      },
      inputText:{
        height:50,
        textAlign: "center",
        fontSize: 15
      },
      arrow:{
          width: 100,
          height: 30,
          marginTop: "5%"
      }
});