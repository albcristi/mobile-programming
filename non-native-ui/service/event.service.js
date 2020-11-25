import {Event} from './../model/event';
import {EventLocation} from './../model/event-location';


export class EventService{
    static events = [
        new Event("Beer Festival",
        "Drinking beer online",
        "dan",
        new EventLocation("Cluj-Napoca",23.578607, 46.769896),
        "2020-11-11",
        "2020-11-12",
        "10:10",
        1
       ),
        new Event("Food mania",
        "lots of food and nice people",
        "dana",
        new EventLocation("Bucharest",26.082503,44.470731),
        "2020-11-12",
        "2020-11-12",
        "13:10",
        2),
        new Event("Titan Park reinvented",
        "paradise for kids and happiness for adults",
        "dana",
        new EventLocation("Bucharest",26.082503,44.470731),
        "2020-11-16",
        "2020-11-20",
        "15:10",
        3),
        new Event("Reading at the terrace",
        "Some good red wine, a good old book and a view over the city",
        "dan",
        new EventLocation("Cluj-Napoca",23.578607, 46.769896),
        "2020-11-11",
        "2020-11-12",
        "20:30",
        4),
        new Event("Programming and pizza",
        "Java, C# or something else? Not a problem, we are all united by the power of pizza",
        "alin",
        new EventLocation("Cluj-Napoca",23.578607, 46.769896),
        "2020-11-11",
        "2020-11-12",
        "20:30",
        5)
    ]


    constructor(){

    }


    eventsFromCity(city){
        result = []
        EventService.events.forEach(value => {
            if(value.location.city === city || city==="all")
            result.push(value)
        })
        return result
    }

    eventsOfUser(username){
        result = []
        EventService.events.forEach(value => {
            if(value.hostUserName === username)
            result.push(value)
        })
        return result
    }

    getEvent(eventId){
        result = null
        EventService.events.forEach(value =>{
            if(value.id === eventId){
                result =value
            }})
        return result
    }

    isOwnedByUser(username, evid){
        let event = this.getEvent(evid)
        if(event==null){
            return false
        }
        return event.hostUserName == username
    }

    removeEvent(evid){
        let index = -1;
        let i = 0;
        EventService.events.forEach(value =>{
            if(value.id === evid){
                index = i;
            }
            i += 1;
        })
        if (index > -1) {
          EventService.events.splice(index, 1);
        }
       
    }

    generateNextId(){
        let id = 1
        EventService.events.forEach(value => id+=1)
        return id
    }

    addEvent(name, description,username, startDate, endDate, startHour, location){
        let id = this.generateNextId()
        let event = new Event(name,
            description,
            username,
            new EventLocation(location, 0,0),
            startDate,
            endDate,
            startHour,
            id
        )
        EventService.events.push(event)
        return event
    }

    updateEvent(name, description,username, startDate, endDate, startHour, location,id){
            this.removeEvent(id);
            let event = new Event(name, description, username, new EventLocation(location,0,0), startDate, endDate, startHour, id);
            EventService.events.push(event);
            return event;
    }

}