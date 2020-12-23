from django.http import QueryDict

from ...service.event_service import UserEventService
from rest_framework.decorators import api_view
from rest_framework.utils import json
from ..utils import *
from ...serializer.model_serializers import UserEventSerializer
import json


@api_view(['GET'])
def has_events(request: HttpRequest, city: str) -> JsonResponse:
    if request.method == 'GET':
        return handle_has_events(city)
    return bad_request()


def handle_has_events(city) ->JsonResponse:
    try:
        service = UserEventService()
        result = service.get_events_from_city(city)
        return return_result({"message": len(result) > 0}, 200)
    except Exception as e:
        return exception_occurred(str(e))


@api_view(['GET'])
def get_events_from_city(request: HttpRequest, city: str) -> JsonResponse:
    if request.method == 'GET':
        return handle_get_events(city)
    return bad_request()


def handle_get_events(city: str) -> JsonResponse:
    try:
        service = UserEventService()
        if city == "all":
            result = service.get_all_events()
        else:
            result = service.get_events_from_city(city)
        result = [UserEventSerializer(event) for event in result]
        result = [event.data for event in result]
        return return_result({"events": result}, 200)
    except Exception as e:
        return exception_occurred(str(e))


@api_view(['GET'])
def get_user_owned_events(request: HttpRequest, user: str) -> JsonResponse:
    if request.method == 'GET':
        return handle_get_user_owned_events(user)
    return bad_request()


def handle_get_user_owned_events(user: str) -> JsonResponse:
    try:
        result = UserEventService().get_all_user_events(user)
        result = [UserEventSerializer(event) for event in result]
        result = [event.data for event in result]
        return return_result({"events": result}, 200)
    except Exception as e:
        return exception_occurred(str(e))


@api_view(['POST'])
def create_event(request: HttpRequest) -> JsonResponse:
    if request.method == 'POST':
        return handle_post_create_event(request)
    return bad_request()


def handle_post_create_event(request: HttpRequest) -> JsonResponse:
    try:
        event = request.POST['event']
        service = UserEventService()
        event = json.loads(event)
        event = service.add_event(event["hostUserName"],
                                  event['location']['city'],
                                  event['location']['longitude'],
                                  event['location']['latitude'],
                                  event['openHour'],
                                  event['startDate'],
                                  event['endDate'],
                                  event['description'],
                                  event['name'])
        if event is None:
            return return_result({"event": -1}, 200)
        return return_result({"event": event.event_id}, 200)
    except Exception as e:
        print(str(e))
        return exception_occurred(str(e))


@api_view(['DELETE', 'PUT'])
def handle_event(request: HttpRequest, event_id: str) -> JsonResponse:
        if request.method == 'DELETE':
            return handle_delete_event(int(event_id))
        if request.method == 'PUT':
            return handle_update_event(request, int(event_id))
        return bad_request()


def handle_delete_event(event_id: int) -> JsonResponse:
    try:
        service = UserEventService()
        result = service.remove_event(event_id)
        return return_result({"result": result}, 200)
    except Exception as e:
        return exception_occurred(str(e))


def handle_update_event(request: HttpRequest, event_id: int) -> JsonResponse:
    try:
        form_data = QueryDict(request.body)
        event = json.loads(form_data["event"])
        title = event["name"]
        description = event["description"]
        city = event["location"]["city"]
        latitude = float(event["location"]["latitude"])
        longitude = float(event["location"]["longitude"])
        start_hour = event["openHour"]
        start_date = event["startDate"]
        end_date = event["endDate"]
        event_id = int(event["serverIdentifier"])
        service = UserEventService()
        result = service.update_event(event_id, city, longitude, latitude, start_hour,
                                      start_date, end_date, description, title)
        return return_result({"result": result}, 200)
    except Exception as e:
        print(str(e))
        return exception_occurred(str(e))
