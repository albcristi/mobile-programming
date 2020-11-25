import {UserApp} from './../model/user';

export class UserService{
    static users = [new UserApp("alin","Alin Pintilie", "parola"),
                    new UserApp("mihai","Mihai Ciubotariu", "parola"),
                    new UserApp("dan","Dan Negru", "parola"),
                    new UserApp("dana","Dana Irimies", "parola")]
    constructor(){

    }

    userNameIsTaken(userName){
        let taken = false;
        UserService.users.forEach(value => {
            if(value.username===userName){
                taken = true;
            }
        })
        return taken;
    }

    verifyLogInData(username, password){
        let taken = false;
        UserService.users.forEach(value => {
            if(value.username==username && value.password==password){
                taken = true;
            }
        })
        return taken;
    }
}