from rest_framework import serializers


class LoginSerializer(serializers.Serializer):
    # 로그인
    id = serializers.CharField(max_length=100, required=True)
    pw = serializers.CharField(max_length=100, required=True)
