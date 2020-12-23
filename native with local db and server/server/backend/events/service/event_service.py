from typing import Optional

from ..repository.event_repository import UserEventRepository
from ..repository.user_repository import UserRepository
from ..models import UserEvent


class UserEventService:
    def __init__(self):
        self.__user_repository = UserRepository()
        self.__event_repository = UserEventRepository()

    def get_all_events(self) -> list:
        return self.__event_repository.get_all_events()

    def get_all_user_events(self, username: str) -> list:
        user = self.__user_repository.get_user(username)
        if user is None:
            return []
        return self.__event_repository.get_user_events(user)

    def get_events_from_city(self, city: str) -> list:
        return self.__event_repository.get_events_from_city(city)

    def remove_event(self, event_id: int) -> bool:
        return self.__event_repository.remove_event(event_id)

    def add_event(self, username: str, city, long, lat, start_hour,
                  start_date, end_date, description, title) -> Optional[UserEvent]:
        host = self.__user_repository.get_user(username)
        if host is None:
            return None
        event = self.__event_repository.add_event(host, city, float(long), float(lat), start_hour,
                                                  start_date, end_date, description, title)
        return event

    def update_event(self, event_id, city: str, long, lat, start_hour,
                     start_date, end_date, description, title) -> bool:
        return self.__event_repository.update_event(event_id, city, long, lat, start_hour,
                                                    start_date, end_date, description, title)
