import React from 'react';
import {StyleSheet, Text, View,  TouchableOpacity, Modal, TouchableHighlight } from 'react-native';
import t from 'tcomb-form-native'; // 0.6.9

const Form = t.form.Form;

const NewEventPart1 = t.struct({
  name: t.String,
  description: t.String,
  location: t.String,
});

const NewEvnetPart2 = t.struct({
  startDate: t.String,
  endDate: t.String,
  startHour: t.String
})


const formStyles = {
    ...Form.stylesheet,
    formGroup: {
        normal: {
          marginBottom: 10,
          backgroundColor: "#000000"
        },
        error: {
          marginBottom: 10
        }
      },
    textBox: {
        normal: {
            backgroundColor: "#000000"
        }
    }
  }

export class NewEventComponent extends React.Component{
    state = {
        name: "",
        show: false,
        showModal: false,
        errors: ["We detected some errors.", "Make sure dates are respecting YYYY-MM-DD format",
         "Hours are in HH:DD [am/pm] format", "Start date is before end date"]
    };




    constructor(props){
        super(props);
    }

    componentDidMount(){
        this.setState({
            parentComponent: this.props.route.params.parentComponent,
            eventId: this.props.route.params.eventId,
            operationDescription: this.props.route.params.operationDescription
        })
    }

    handleSubmit = () =>{
        const value = this._form.getValue(); // use that ref to get the form value
        if(value == null)
            return;
        let validated = this.validateStringField(value.name);
        validated = validated && this.validateStringField(value.description);
        validated = validated && this.validateStringField(value.location)
        if(validated)
            this.setState({
                id: this.state.eventId,
                name: value.name,
                description: value.description,
                location: value.location,
                show: validated
            })
        
    }

    handleFinalSubmit = () =>{
        const value = this._form2.getValue(); // use that ref to get the form value
        if(value == null)
            return;

        let validated = this.validateStringField(value.startDate);
        validated = validated && this.validateStringField(value.endDate);
        validated = validated && this.validateStringField(value.startHour);
        validated = validated && this.validateHourField(value.startHour);
        validated = validated && this.validateDateField(value.startDate);
        validated = validated && this.validateDateField(value.endDate);
        validated = validated && (value.startDate <= value.endDate);

        if(validated){
            let startDate = value.startDate;
            let endDate = value.endDate;
            let startHour = value.startHour;
            let event = {
               name: this.state.name,
               description: this.state.description,
               location: this.state.location,
               startDate: startDate,
               startHour: startHour,
               endDate: endDate
            };

            this.props.navigation.navigate(this.state.parentComponent, {newData: event});
        }
        else{
            this.setState({
                showModal: true
            })
        }
    }

    validateStringField(val){
        try{
            if(val.length >= 1)
                return true
            return false
        }
        catch(e){
            return false
        }
    }

    validateDateField(value){
        let regex1 = new RegExp("^[1-9][0-9]{3}-[0-9][1-9]-[0-9][1-9]\$");
        let regex3 = new RegExp("^[1-9][0-9]{3}-[0-9][1-9]-[1-9][0-9]\$");
        let regex2 = new RegExp("^[1-9][0-9]{3}-[1-9][0-9]-[1-9][0-9]\$");
        let regex4 = new RegExp("^[1-9][0-9]{3}-[1-9][0-9]-[0-9][1-9]\$");
        return regex1.test(value) || 
               regex2.test(value) ||
               regex3.test(value) ||
               regex4.test(value);
    }

    validateHourField(value){
        let regex1 = new RegExp("^(0?[1-9]|1[0-2]):[0-5][0-9]\$")
        let regex2 = new RegExp("((1[0-2]|0?[1-9]):([0-5][0-9]) ?([AaPp][Mm]))")
        let regex3 = new RegExp("^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$")
        let regex4 = new RegExp("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\$")
        let regex5 = new RegExp("(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)")
        return regex1.test(value) || 
               regex2.test(value) ||
               regex3.test(value) ||
               regex4.test(value) ||
               regex5.test(value);
    }

    render(){

        return (
                <View style={styles.screenContainer}>
                    <View style={styles.positionCenter}>
                        <Text style={styles.upperBarText}>
                            {this.state.operationDescription}
                        </Text>
                    </View>
                    <View style={styles.formContainer}>
                        {!this.state.show &&
                            <View>
                            <Form  type={NewEventPart1}
                                    ref={(c) => (this._form = c)}
                                    style={formStyles}/>

                            <View style={styles.positionCenter}>
                                <TouchableOpacity
                                            onPress={this.handleSubmit}
                                            style={styles.buttonStyle}>
                                    <Text>Next Data</Text>
                                </TouchableOpacity>
                            </View>
                            </View>
                        }
                        {this.state.show &&
                            <View>
                                <Form  type={NewEvnetPart2}
                                        ref={(c) => (this._form2 = c)}
                                        style={formStyles}/>

                                <View style={styles.positionCenter}>
                                    <TouchableOpacity
                                                onPress={this.handleFinalSubmit}
                                                style={styles.buttonStyle}>
                                        <Text>Save Changes</Text>
                                    </TouchableOpacity>
                                </View>
                            </View>
                        }
                        <View>
                            <View style={styles.centeredView}>
                                    <Modal
                                    animationType="slide"
                                    transparent={true}
                                    visible={this.state.showModal}
                                    onRequestClose={() => {
                                    }}
                                    >
                                        <View style={styles.centeredView}>
                                            <View style={styles.modalView}>
                                            <Text style={styles.modalText}>Add Failed</Text>
                                            <Text style={styles.modalText}>
                                                {this.state.errors[0]}
                                            </Text>
                                            <Text style={styles.modalText}>
                                                {this.state.errors[1]}
                                            </Text>
                                            <Text style={styles.modalText}>
                                                {this.state.errors[2]}
                                            </Text>
                                            <Text style={styles.modalText}>
                                                {this.state.errors[3]}
                                            </Text>
                        
                                            <TouchableHighlight
                                                style={{ ...styles.openButton, backgroundColor: "#2196F3" }}
                                                onPress={() => {
                                                this.setState({showModal: false})
                                                }}
                                            >
                                                <Text style={styles.textStyle}>OK </Text>
                                            </TouchableHighlight>
                                            </View>
                                        </View>
                                    </Modal>
                            </View>
                        </View>
                    </View>
                </View>
            
        )
    }
}


const styles = StyleSheet.create(
    {
        screenContainer: {
            flex: 1,
            backgroundColor: '#B1CCEB',
            paddingTop: "7%"
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
        listItemText:{
            fontSize: 20
        },
        listBigCont:{
            flex: 1,
        },
        positionCenter: {
            marginLeft: "35%"
        },
        buttonStyle:{
            borderRadius:10,
            borderColor: "blue",
            borderWidth: 1,
            padding: "1%",
            height:40,
            alignItems:"center",
            justifyContent:"center",
            marginTop:5,
            width: 100
        }, formContainer: {
            marginTop: "26%",
            padding: "3%",
            borderRadius: 5,
            backgroundColor: "white"
        },
        centeredView: {
            flex: 1,
            justifyContent: "center",
            alignItems: "center",
            marginTop: 22
          },
          modalView: {
            margin: 20,
            backgroundColor: "white",
            borderRadius: 20,
            padding: 35,
            alignItems: "center",
            shadowColor: "#000",
            shadowOffset: {
              width: 0,
              height: 2
            },
            shadowOpacity: 0.25,
            shadowRadius: 3.84,
            elevation: 5
          },
          openButton: {
            backgroundColor: "#F194FF",
            borderRadius: 20,
            padding: 10,
            elevation: 2
          },
          textStyle: {
            color: "white",
            fontWeight: "bold",
            textAlign: "center"
          },
          modalText: {
            marginBottom: 15,
            textAlign: "center"
          }
    }
    
)