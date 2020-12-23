from django.http import JsonResponse, HttpRequest


def bad_request() -> JsonResponse:
    return JsonResponse({'error': 'BAD REQUEST'}, status=401)


def exception_occurred(error_message: str) -> JsonResponse:
    return JsonResponse({'error': error_message}, status=401)


def return_result(data: dict, status_code: int) -> JsonResponse:
    return JsonResponse(data, status=status_code)
