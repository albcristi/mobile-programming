# Task
Describe your project idea from the point of view of a client.
The client is not having any technical skills but has a very good idea of why he needs a mobile application for his users.
The details should cover the following requirements:
- The application should offer CRUD operations on the business objects. The main screen should be a list of the business objects and it should allow at least the following operations:
   - Create - create new elements that will be added to the list.
   - Read - present the elements in a list.
   - Update - allow the user to update an element found in the list.
   - Delete - delete existing elements from the list.
- The application should persist the content locally, to be able to allow offline access. And while online it should synchronize the content with a remote server (implemented also by you, in the language of your choice, no serverless solutions are allowed yet).
- The communication with the server should be managed using REST services, and it should have counterpart operations for each of the operations mentioned above.
- While offline the application will perform the following actions:
   - On update and delete - a message will be displayed, that the application is offline and the operation is not available.
   - On read - the content from the local database will be displayed.
   - On create - the input will be persisted in the local database and when the application will detect that the device is able to connect to the server will push all the created entries, while the device was offline.
- Create mockup screens for CRUD operations. Here you can use any mockup tool, we recommend figma.com

# Project Idea

### The city in your pocket. 

We want to have a new mobile application that allows our users to see events createad by other users in the area near them. For example, a user from Cluj-Napoca may want to go out for the night, but its out of ideas so he goes in our app, sees what events are happening near him and may decide to give them a try. Maybe you represent a big festival and want to make an annoucement regarding a following event, just enter our app and create the event. It has never been easier to see all the events from your city in one place. In order to use the app a user needs to log in or to create an account for free and then he may explore all the available events right from their home. A user is also able to create events. In order to create a new event, you need to be logged to our app and to provide some data about the event, namely the name of the event, location, starting/ending date, description and some additional information like the official site. A user may also modify the details concerning their events or also remove them. All users should be able to see a list with the registered events, this list may include for each event a name and the person that created the event, and in case the user is intrested in the event he may simply touch the name of the event from the list in order to see the full list of details regarding that event. If the current user is the creator of the currently selected event he shoukd be provided with the options of updating the details of the event or even removing the event.
