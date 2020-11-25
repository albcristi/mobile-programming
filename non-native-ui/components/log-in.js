import React from 'react';
import { Image, StyleSheet, Text, View, TextInput, TouchableOpacity } from 'react-native';
import Dialog from "react-native-dialog";
import {UserService} from './../service/user.service';

export class LogInComponent extends React.Component{
    state = {
        username: "",
        password: "",
        logFailed: false
    }

    constructor(props){
      super(props)
      this.userService = new UserService()
    }
    
    handleLogIn(){
      let logStatus = this.userService.verifyLogInData(this.state.username, this.state.password)
      if(logStatus == true){
        // log in is a success
        this.props.navigation.navigate("SelectCity", {user: this.state.username})

      }
      else{
           this.setState({logFailed: true})
      }
    }

    componentWillUnmount(){
      this.setState({
        username: " ",
        password: " "
      })
      this.userService = new UserService()
    }
    render(){
  
        return (
            <>
            <View style={styles.container}>
                <Text style={styles.logo}>City in your pocket</Text>
                <View style={styles.inputView} >
                    <TextInput  
                        style={styles.inputText}
                        placeholder="username" 
                        placeholderTextColor="#003f5c"
                        onChangeText={text => this.setState({username:text})}/>
                </View>
                <View style={styles.inputView} >
                    <TextInput  
                        secureTextEntry
                        style={styles.inputText}
                        placeholder="password" 
                        placeholderTextColor="#003f5c"
                        onChangeText={text => this.setState({password:text})}/>
                </View>
                <Image
                    source={require('./img.png')} 
                    style={styles.image}/>
                <TouchableOpacity style={styles.loginBtn}
                      onPress={() => {this.handleLogIn()}}>
                     <Text style={styles.loginText}>LOG IN</Text>
                </TouchableOpacity>
                <TouchableOpacity
                   onPress={()=>{ this.props.navigation.navigate('Register')}}>
                     <Text>Become a member</Text>
                </TouchableOpacity>
                <Dialog.Container visible={this.state.logFailed}>
                    <Dialog.Title>Log In Failed</Dialog.Title>
                    <Dialog.Description>Make sure introduced data is correct</Dialog.Description>
                    <Dialog.Button label="OK" onPress={() => this.setState({logFailed: false})} />
               </Dialog.Container>
            </View>
           </>
        )
    }
}


const styles = StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: '#B1CCEB',
      alignItems: 'center',
      justifyContent: 'center',
    },
    logo:{
      fontSize:25,
      marginBottom:40
    },
    inputView:{
      width:"80%",
      height:50,
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
    loginBtn:{
      width:"80%",
      backgroundColor:"#93B8E4",
      borderRadius:10,
      height:50,
      alignItems:"center",
      justifyContent:"center",
      marginTop:20,
      width: 100,
      marginBottom:40
    },
    image:{
        height: "50%",
        width: "100%",
        position: 'absolute', 
        bottom: 0, 
    }
    
  });