from rest_framework import serializers
from ..models import ApplicationUser, UserEvent


class ApplicationUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = ApplicationUser
        fields = [
            'username',
            'password',
            'full_name'
        ]


class UserEventSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserEvent
        fields = [
            'event_id',
            'title',
            'host',
            'city',
            'long',
            'lat',
            'start_hour',
            'start_date',
            'end_date',
            'description'
        ]
