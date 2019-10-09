from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .serializers import LoginSerializer
from selenium import webdriver
from bs4 import BeautifulSoup
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException

options = webdriver.ChromeOptions()
options.add_argument('headless')
options.add_argument('window-size=1920x1080')
options.add_argument('disable-gpu')
driver = webdriver.Chrome('utils/chromedriver', chrome_options=options)


class Table(APIView):
    # E-Attendance 과목 반환
    # POST 요청만 허용

    serializer_class = LoginSerializer

    def get(self, request, format=None):
        # Error
        return Response({
            'error': 'Please request by POST with id and password',
        }, status=status.HTTP_400_BAD_REQUEST)

    def post(self, request):
        serializer = self.serializer_class(data=request.data)

        if serializer.is_valid():
            id = serializer.validated_data.get('id')
            pw = serializer.validated_data.get('pw')

            return get_table(request, id, pw)

        else:
            return Response({
                'error': 'Please give validate id and password',
            }, status=status.HTTP_400_BAD_REQUEST)


def get_table(request, USER_ID, USER_PW):
    LOGIN_URL = 'https://scard1.snu.ac.kr'

    test = 'result:'
    driver.get(LOGIN_URL)
    try:
        id_ = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "input.ip_id")))
        pw_ = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "input.ip_pw")))
        login_ = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "div.login_btn")))
        test += ' login success,'
    except TimeoutException:
        test += ' login fail,'

    id_.send_keys(USER_ID)
    pw_.send_keys(USER_PW)
    login_.click()
    # 로그인 실패시 에러 반환
    if driver.current_url == "https://sso.snu.ac.kr/nls3/error.jsp?errorCode=5402":
        return Response({
            'error': 'The user info was not matched'
        }, status=status.HTTP_400_BAD_REQUEST)

    driver.get('https://scard1.snu.ac.kr/eams/student/timetbl/timetblLst')
    try:
        span_ = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "span.tit")))
        test += ' table success,'
    except TimeoutException:
        test += ' table fail,'
    html = driver.page_source
    soup = BeautifulSoup(html, 'html.parser')

    driver.get('https://sso.snu.ac.kr/snu/ssologoff.jsp')
    # driver.quit()
    try:
        WebDriverWait(driver, 3).until(EC.alert_is_present())

        alert = driver.switch_to.alert
        alert.accept()
        test += ' alert success'
    except TimeoutException:
        test += ' alert fail'

    class_list = soup.select('span.tit a')
    target_list = []

    for item in class_list:
        is_duplicated = False
        tmp = item.attrs['title']
        title = tmp[tmp.find('교과목명') + 7: tmp.find('주담당') - 1]
        time_tmp = tmp[tmp.rfind('강의시간') + 7:]
        day = time_tmp[1:2]
        start_time = time_tmp[3:8]

        # 중복되는 수업 검사
        for target_item in target_list:
            if target_item['title'] == title and target_item['day'] == day and \
                    target_item['start_time'] == start_time:
                is_duplicated = True
                break

        if is_duplicated:
            continue

        target = {
            'title': title,
            'day': day,
            'start_time': start_time,
        }
        target_list.append(target)

    return Response({
        'list': target_list,
        'result': test,
    })

