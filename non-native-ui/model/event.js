

export class Event{
    constructor(name, description, hostUserName, location, startDate, endDate, openHour, id){
        this.name = name
        this.description = description
        this.hostUserName = hostUserName
        this.location = location
        this.startDate = startDate
        this.endDate = endDate
        this.openHour = openHour
        this.id = id
    }
}