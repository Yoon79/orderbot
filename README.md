# OrderBot - 안드로이드 순서 관리 앱

OrderBot은 사용자가 항목을 추가하고 순서를 조정할 수 있는 안드로이드 앱입니다.

## 주요 기능

- **항목 추가**: 상단에 항목을 입력하고 추가할 수 있습니다
- **순서 조정**: 각 항목의 오른쪽에 있는 화살표 버튼으로 위아래로 이동할 수 있습니다
- **항목 삭제**: 각 항목의 삭제 버튼으로 항목을 제거할 수 있습니다
- **순서 표시**: 각 항목에 1번부터 순차적으로 번호가 표시됩니다

## 기술 스택

- **언어**: Kotlin
- **최소 SDK**: API 21 (Android 5.0)
- **타겟 SDK**: API 33 (Android 13)
- **UI**: Material Design Components
- **아키텍처**: RecyclerView를 사용한 리스트 관리

## 프로젝트 구조

```
app/
├── src/main/
│   ├── java/com/example/orderbot/
│   │   ├── MainActivity.kt          # 메인 액티비티
│   │   ├── OrderAdapter.kt          # RecyclerView 어댑터
│   │   └── OrderItem.kt             # 데이터 클래스
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml    # 메인 화면 레이아웃
│   │   │   └── item_order.xml       # 항목 레이아웃
│   │   ├── drawable/                # 아이콘 및 배경
│   │   ├── values/
│   │   │   ├── strings.xml          # 문자열 리소스
│   │   │   ├── colors.xml           # 색상 리소스
│   │   │   └── themes.xml           # 앱 테마
│   └── AndroidManifest.xml          # 앱 매니페스트
├── build.gradle                     # 앱 모듈 빌드 설정
└── proguard-rules.pro              # ProGuard 규칙

build.gradle                         # 프로젝트 레벨 빌드 설정
settings.gradle                      # 프로젝트 설정
gradle.properties                    # Gradle 속성
```

## 빌드 및 실행

1. Android Studio에서 프로젝트를 엽니다
2. Gradle 동기화를 기다립니다
3. 안드로이드 기기나 에뮬레이터에서 앱을 실행합니다

## 사용법

1. **항목 추가**: 상단 입력 필드에 항목을 입력하고 "추가" 버튼을 누릅니다
2. **순서 변경**: 항목 오른쪽의 ↑↓ 화살표 버튼을 사용하여 순서를 조정합니다
3. **항목 삭제**: 항목 오른쪽의 휴지통 아이콘을 눌러 항목을 삭제합니다

## 플레이스토어 배포 준비

앱을 플레이스토어에 배포하려면:

1. `app/build.gradle`에서 `versionCode`와 `versionName`을 업데이트
2. 서명된 APK 또는 AAB 생성
3. Google Play Console에서 앱 등록 및 업로드

## 라이선스

이 프로젝트는 Apache License 2.0 하에 배포됩니다.
