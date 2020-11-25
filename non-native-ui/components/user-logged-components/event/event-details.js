import React from 'react';
import { TouchableOpacity, StyleSheet, Text, View, SafeAreaView} from 'react-native';
import { EventService } from '../../../service/event.service';
import MapView from 'react-native-maps';



export class EventDetailsComponent extends React.Component{

    state = {
        username: "",
        event: null,
        isowner: false
    }

    constructor(props){
        super(props)
        this.eventService = new EventService()
    }

    componentDidMount(){
      this.setState({
          user: this.props.route.params.username,
          event: this.eventService.getEvent(this.props.route.params.evid),
          isowner: this.eventService.isOwnedByUser(this.props.route.params.username, this.props.route.params.evid)
      })
    }

    handleDeleteEvent(){

    }


    render(){

        return(
            <>  
                <View style={styles.container}>
                    {this.state.event!==null &&
                        <View>
                            <Text style={styles.logo}>
                                    {this.state.event.name}
                            </Text>
                            <Text style={styles.miniLogo}>
                                {this.state.event.description}
                            </Text>
                            <Text style={styles.detailsText}>
                                {this.state.event.location.city}
                            </Text>
                            <Text style={styles.detailsText}>
                                Opening date: {this.state.event.openHour}, {this.state.event.startDate}
                            </Text>
                            <Text style={styles.detailsText}>
                                End date: {this.state.event.endDate}
                            </Text>
                        
                            <Text style={styles.footerText}>
                                Hosted by @{this.state.event.hostUserName}
                            </Text>
                        </View>
                   }
                </View>
            </>
        )
    }
}


const styles = StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: '#B1CCEB',
      paddingLeft: "5%",
      paddingRight: "5%",
      paddingTop: "15%",
      width:"100%",
      height: "100%"
    },
    logo:{
      marginTop: 20,
      fontSize:30,
      marginBottom:30,
      textAlign: "center"
    },
    miniLogo: {
        fontSize:25,
        paddingBottom: "25%",
        paddingTop: "5%",
        textAlign: "center"
    },
    detailsText:{
        fontSize: 20,
        textAlign: "center"
    },
    footerText:{
        fontSize: 20,
        textAlign: "center",
        marginTop: "30%"
    },
    someButton:{
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
    buttonContainer: {
        flex: 1,
        flexDirection: "row",
        marginBottom: "5%"
    },
    buttonSt:{
        width:"80%",
      backgroundColor:"#93B8E4",
      borderRadius:10,
      height:50,
      alignItems:"center",
      justifyContent:"center",
      marginTop:20,
      width: 100,
      marginBottom:40
    }
  });