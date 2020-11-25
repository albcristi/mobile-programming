import React from 'react';
import {CheckBox,Image, StyleSheet, Text, View, TextInput, TouchableOpacity } from 'react-native';


export class RegisterComponent extends React.Component{
    state = {
        username: "",
        fullname: "",
        password: "",
        reentered: "",
        isAdult: false
    }

    constructor(props){
      super(props)
    }
    
    render(){
  
        return (
            <>
            <View style={styles.container}>
                <Text style={styles.logo}>City in your pocket</Text>
                <View style={styles.inputView} >
                    <TextInput  
                        style={styles.inputText}
                        placeholder="full name" 
                        placeholderTextColor="#003f5c"
                        onChangeText={text => this.setState({fullname:text})}/>
                </View>
                <View style={styles.inputView} >
                    <TextInput  
                        style={styles.inputText}
                        placeholder="user name" 
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

                <View style={styles.inputView} >
                    <TextInput  
                        secureTextEntry
                        style={styles.inputText}
                        placeholder="re-enter password" 
                        placeholderTextColor="#003f5c"
                        onChangeText={text => this.setState({reentered:text})}/>
                </View>
                <View style={styles.checkboxContainer}>
                    <CheckBox
                        value={this.state.isAdult}
                        onChange={() => this.setState({isAdult: !this.state.isAdult})}
                        style={styles.checkbox}
                    />
                    <Text style={styles.label}>I am 18yrs or older</Text>
                </View>
                <Image
                    source={require('./img.png')} 
                    style={styles.image}/>
                <TouchableOpacity style={styles.registerBtn}>
                     <Text style={styles.loginText}>REGISTER</Text>
                </TouchableOpacity>
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
    checkboxContainer: {
        flexDirection: "row",
        marginBottom: 20,
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
    registerBtn:{
      backgroundColor:"#93B8E4",
      borderRadius:10,
      height:50,
      alignItems:"center",
      justifyContent:"center",
      marginTop:20,
      width: 150,
      marginBottom:40
    },
    image:{
        height: "50%",
        width: "100%",
        position: 'absolute', 
        bottom: 0, 
    },
    checkbox: {
        alignSelf: "center",
    },
    label: {
    margin: 8,
  }
    
  });