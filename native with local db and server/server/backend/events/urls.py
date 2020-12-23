from django.contrib import admin
from django.urls import path, include
from .views import *

urlpatterns = [
    path('', index, name='welcome message'),
    path('user/', include('events.controller.user.urls')),
    path('event/', include('events.controller.events.urls'))
]
