import React from 'react';
import { Image, StyleSheet, Text, View, FlatList, TouchableOpacity, Button } from 'react-native';
import {EventService} from './../../../service/event.service';


export class UserOwnedEventsComponent extends React.Component{
    state = {
        username: "",
        city: "",
        events: [],
        deletes: [],
        updates: [],
        currentID: -1,
    }

    constructor(props){
        super(props)
        this.eventService = new EventService()

    }

    componentDidMount(){
        this.setState({
            username: this.props.route.params.username,
            city: this.props.route.params.city,
            events: this.eventService.eventsOfUser(this.props.route.params.username)
        })
    }

    componentDidUpdate(){
        try{
            let newData =  this.props.route.params.newData;
            if(newData !== undefined){
                this.handleUpdate(newData);
                this.props.route.params.newData = undefined;
            }
        }
        catch(e){
            console.log(e);
        }
    }

    eventDetails(evid){
        this.props.navigation.navigate("EventDetails",{username: this.state.username,evid: evid})
    }


    handleRemove(itemId){
        this.eventService.removeEvent(itemId);
        let events = this.state.events;
        let index = -1;
        let i = 0;
        events.forEach(value =>{
            if(value.id === itemId){
                index = i;
            }
            i += 1;
        })
        if(index>-1){
            events.splice(index, 1);
        }
        this.setState({
            events: events,
            deletes: [...this.state.deletes, itemId]
        });
    }


    handleUpdate(newData){
        let event = this.eventService.updateEvent(newData["name"],
                    newData["description"], this.state.username,
                    newData["startDate"], newData["endDate"], 
                    newData["startHour"], newData["location"], this.state.currentID)
        let itemId = this.state.currentID
        let events = this.state.events;
        let index = -1;
        let i = 0;
        events.forEach(value =>{
            if(value.id === itemId){
                index = i;
            }
            i += 1;
        })
        if(index>-1){
            events.splice(index, 1);
        }
        this.setState({
            events: [...events, event],
            updates: [...this.state.updates, itemId]
        });
    }

    goToEventsPage(){
        
        this.props.navigation.navigate("EventListCity", {
            deletes: this.state.deletes,
            updates: this.state.updates
        })
    }

    handleEventUpdate(eventID){
        this.setState({
            currentID: eventID
        })
        this.props.navigation.navigate("NewEvent",
        {parentComponent:"OwnedEvents", eventId: eventID,
        operationDescription: "Update Event"})
    }


    render(){
            return (
                <>
                   <View style={styles.screenContainer}>
                        <TouchableOpacity style={styles.upperBar} onPress={()=>{this.goToEventsPage()}}>
                                        <Text>Go back</Text>
                        </TouchableOpacity>
                        <View style={styles.upperBar}>
                            <Text style={styles.upperBarText}>Hello, @{this.state.username}!</Text>
                        </View>
                        {this.state.events.length === 0 &&
                            <Text style={{marginTop: "10%", marginLeft: "5%",marginRight: "5%",fontSize: 25, textAlign: "center"}}>You don't have any created events yet :(</Text>
                        }
                        <View style={styles.listContainer}>
                        {this.state.events.length > 0 &&
                            <FlatList style={styles.listContainer}
                            keyExtractor={(item) => `item.id}`}
                            data={this.state.events}
                            renderItem={({item})=>
                            (<View style={styles.listItemContainer}>
                                <View style={styles.listBigCont}>
                                    <TouchableOpacity onPress={()=>{this.eventDetails(item.id)}}>
                                        <Text style={styles.listItemText}>Event: {item.name}</Text>
                                        <Text style={styles.listItemText}>Location: {item.location.city}</Text>
                                        <Text style={styles.listItemText}>Hosted by: {item.hostUserName}</Text>
                                    </TouchableOpacity>
                                </View>
                                <View>
                                <View>
                                    <TouchableOpacity style={styles.buttonDelete} onPress={()=>{this.handleRemove(item.id)}}>
                                        <Text>Remove</Text>
                                    </TouchableOpacity>
                                    <TouchableOpacity style={styles.buttonDelete} onPress={()=>{this.handleEventUpdate(item.id)}}>
                                        <Text>Update</Text>
                                    </TouchableOpacity>
                                </View>
                                </View>
                            </View>)}/>
                        }
                        </View>
                    
                   </View>
                </>
         )
    }
}



const styles = StyleSheet.create(
    {
        screenContainer: {
            flex: 1,
            backgroundColor: '#B1CCEB',
            paddingTop: "5%"
        },
        upperBar:{
            flex: 1, 
            flexDirection: 'row',
            justifyContent: "space-between",
            marginLeft: "10%",
            marginRight: "10%",
        },
        upperBarText: {
            fontSize: 25
        },
        listContainer:{
            flex: 7
        },
        listItemContainer: {
            flex: 1,
            marginLeft: "5%",
            flexDirection: "row",
            marginRight: "5%",
            marginBottom: "2%",
            backgroundColor: "#f5f5f5",
            padding: "5%",
            borderRadius: 5,
        },
        listItemText:{
            fontSize: 20
        },
        listBigCont:{
            flex: 1,
        },
        buttonDelete:{
            backgroundColor:"#93B8E4",
            borderRadius:10,
            height:40,
            alignItems:"center",
            justifyContent:"center",
            marginTop:5,
            width: 100
        }
    }
)