from django.contrib import admin
from django.urls import path
from .event_controller import *

urlpatterns = [
    path('has-events/city=<str:city>', has_events, name='retrieves if there are events in a city'),
    path('city-events/city=<str:city>', get_events_from_city, name='retrieves events from a city'),
    path('owned-events/user=<str:user>', get_user_owned_events, name='retrieves events owned by user'),
    path('new-event', create_event, name='adds a new event'),
    path('instance/<str:event_id>', handle_event, name='handles events')
]
