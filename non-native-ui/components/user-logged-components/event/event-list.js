import React from 'react';
import { Image, StyleSheet, Text, View, FlatList, TouchableOpacity } from 'react-native';
import {EventService} from './../../../service/event.service';
import Dialog from "react-native-dialog";
import { EventLocation } from '../../../model/event-location';

export class EventListComponent extends React.Component{
    state = {
        loggedUser: "",
        addDone: false,
        showD: false,
        updateElem: false,
        events: []
    }

    constructor(props){
        super(props)
        this.eventService = new EventService()

    }


    componentDidMount(){

        this.setState({
            city: this.props.route.params.city,
            loggedUser: this.props.route.params.username,
            events: this.eventService.eventsFromCity(this.props.route.params.city)
        })

    }

    componentDidUpdate(){
        try{
            toBeDeleted = this.props.route.params.deletes;
            if(toBeDeleted !== undefined && toBeDeleted.length > 0){
                toBeDeleted.forEach(element => {
                    this.handleRemove(element,(events) => { this.setState({events});});
                });
            }
            newData = this.props.route.params.newData;
            if(newData !== undefined){
                let event = this.eventService.addEvent(newData["name"],
                    newData["description"], this.state.loggedUser,
                    newData["startDate"], newData["endDate"],
                    newData["startHour"], newData["location"]);
                this.shouldAdd(event);
                this.props.route.params.newData = undefined;
            }
            toBeUpdated = this.props.route.params.updates;
            if(toBeUpdated !== undefined){
                this.handleUpdate(toBeUpdated)
                this.props.route.params.updates = undefined;
            }

        }
        catch(e){
            console.log(e)
        }
    }

    handleShowUserOwnedEvents(){
        this.props.navigation.navigate('OwnedEvents',{username: this.state.loggedUser, city: this.state.city})
    }

    handleEventDetails(evid){
        this.props.navigation.navigate("EventDetails",{username: this.state.loggedUser,evid: evid})
    }


    handleRemove(evid, clbck){
        let events = this.state.events;
        let index = -1;
        let i = 0;
        events.forEach(value =>{
            if(value.id === evid){
                index = i;
            }
            i += 1;
        });
        if(index>-1){
            events.splice(index, 1);
        }
       clbck(events);
       return events;
    }

    handleUpdate(elements){
       let events = this.state.events
       elements.forEach(element => {
            events = this.handleRemove(element, (events) => {});
            events.push(this.eventService.getEvent(element));
       });

       this.setState({
           events: events
       })
       this.setState({
           events: this.eventService.eventsFromCity(this.state.city)
       })
    }


    shouldAdd(event){
        if(event.location.city===this.state.city || this.state.city==="all")
                this.setState({
                    events: [...this.state.events, event]
                })
    }


    handleNewEvent(){
        if(this.state.addElem){
            let event = this.eventService.addEvent(this.state.name,this.state.description,this.state.loggedUser,this.state.startDate,this.state.endDate,this.state.startHour,this.state.cityt)
            this.setState({
                events: this.shouldAdd(event),
                showD: false,
                addElem: false,
                updateElem: false
            })
        }

        this.setState({
            showD: false,
            addElem: false,
            updateElem: false
        })
    }


    goToNewEventScreen(){
        this.props.navigation.navigate("NewEvent",
        {parentComponent:"EventListCity", eventId: -1,
        operationDescription: "Create Event"})
    }

    render(){

        return (
            <>
               <View style={styles.screenContainer}>
                    <View style={styles.upperBar}>
                        <Text style={styles.upperBarText}>Events</Text>
                        <TouchableOpacity onPress={()=>{this.handleShowUserOwnedEvents()}}>
                             <Text style={styles.upperBarText}>Your Events:</Text>
                        </TouchableOpacity>
                    </View>
                    <View style={styles.listContainer}>
                    {this.state.events.length > 0 &&
                        <FlatList style={styles.listContainer}
                        keyExtractor={(item) => `${item.id}n`}
                        data={this.state.events}
                        renderItem={({item})=>
                        (<View style={styles.listItemContainer}>
                            <View style={styles.listBigCont}>
                                <TouchableOpacity onPress={() => {this.handleEventDetails(item.id)}}>
                                    <Text style={styles.listItemText}>Event: {item.name}</Text>
                                    <Text style={styles.listItemText}>Location: {item.location.city}</Text>
                                    <Text style={styles.listItemText}>Hosted by: {item.hostUserName}</Text>
                                </TouchableOpacity>
                            </View>

                        </View>)}/>
                    }
                    </View>
               <TouchableOpacity style={styles.buttonAdd} onPress={()=>{
                   this.goToNewEventScreen()
               }}>
                                <Text>Add</Text>
                </TouchableOpacity>
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
            flexDirection: "row",
            marginLeft: "5%",
            marginRight: "5%",
            marginBottom: "2%",
            backgroundColor: "#f5f5f5",
            padding: "5%",
            borderRadius: 5,
        },
        listItemText:{
            fontSize: 20
        }, listBigCont:{
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
        },
        buttonAdd:{
            backgroundColor:"#93B8E4",
            borderRadius:10,
            height:40,
            alignItems:"center",
            justifyContent:"center",
            marginTop:5,
            width: 100,
            marginLeft: "70%",
            marginBottom: 10
        }
    }
)
