from django.urls import path
from api import views


urlpatterns = [
    path('table/', views.Table.as_view()),
]
