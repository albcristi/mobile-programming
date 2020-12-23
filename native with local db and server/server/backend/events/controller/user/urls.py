from django.contrib import admin
from django.urls import path
from .user_controller import *


urlpatterns = [
    path('log-in/user=<str:username>', user_log_in, name='performs user log in'),
    path('availability/user=<str:username>', username_available, name='checks if user name is available'),
    path('new-account', create_user, name='creates a new user')
]
