from ...service.user_service import UserService
from rest_framework.decorators import api_view
from rest_framework.utils import json
from ..utils import *


@api_view(['POST'])
def user_log_in(request, username: str) -> JsonResponse:
    if request.method == 'POST':
        return handle_post_user_log_in(request, username)
    return bad_request()


def handle_post_user_log_in(request: HttpRequest, user_name) -> JsonResponse:
    try:
        password = request.POST["password"]
        service = UserService()
        result = service.do_log_in(user_name, password)
        return return_result({"logged": result}, 200)
    except Exception as e:
        return exception_occurred(str(e))


@api_view(['GET'])
def username_available(request, username: str) -> JsonResponse:
    if request.method == 'GET':
        return handle_get_username_available(username)
    return bad_request()


def handle_get_username_available(username) -> JsonResponse:
    try:
        service = UserService()
        unique = service.username_is_unique(username)
        return return_result({"unique": unique}, 200)
    except Exception as e:
        return exception_occurred(str(e))


@api_view(['POST'])
def create_user(request: HttpRequest) ->JsonResponse:
    if request.method == 'POST':
        return handle_post_create_user(request)
    return bad_request()


def handle_post_create_user(request: HttpRequest) -> JsonResponse:
    try:
        username = request.POST["username"]
        password = request.POST["password"]
        full_name = request.POST["full_name"]
        service = UserService()
        return return_result({"created": service.add_user(username, password, full_name)}, 200)
    except Exception as e:
        return exception_occurred(str(e))
