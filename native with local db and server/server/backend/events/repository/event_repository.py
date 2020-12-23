from typing import Optional

from ..models import UserEvent, ApplicationUser


class UserEventRepository:

    def get_all_events(self) -> list:
        return list(UserEvent.objects.all())

    def get_user_events(self, user: ApplicationUser) -> list:
        return list(user.user_events.all())

    def get_events_from_city(self, city: str) -> list:
        return list(UserEvent.objects.filter(city=city))

    def get_event(self, event_id: int) -> Optional[UserEvent]:
        try:
            return UserEvent.objects.get(pk=event_id)
        except Exception:
            return None

    def remove_event(self, event_id: int) -> bool:
        event = self.get_event(event_id)
        if event is None:
            return False
        event.delete()
        return True

    def add_event(self, host: ApplicationUser, city: str, long, lat, start_hour,
                  start_date, end_date, description, title) -> Optional[UserEvent]:
        try:
            event = UserEvent(
                title=title,
                host=host,
                city=city,
                long=long,
                lat=lat,
                start_hour=start_hour,
                start_date=start_date,
                end_date=end_date,
                description=description
            )
            event.save()
            return event
        except Exception:
            return None

    def update_event(self, event_id, city: str, long, lat, start_hour,
                     start_date, end_date, description, title) -> bool:
        event = self.get_event(event_id)
        if event is None:
            return False
        event.title = title
        event.city = city
        event.long = long
        event.lat = lat
        event.start_hour = start_hour
        event.start_date = start_date
        event.end_date = end_date
        event.description = description
        event.save()
        return True
