# Team-Meoguri-Linkocean-BE

## 용례

### Ultimate (궁극의) <br>

북마크, 프로필 필터링 조회 로직을 통합 하는 과정에서 등장한 모든것을 포괄한다는 의미를 담은 Prefix 리팩터링 이후에는 Ultimate 글자를 제거한다.

예를 들어 `UltimateBookmarkFindCond` 는

- `BookmarkFindCond`,
- `FeedBookmarksSearchCond`,
- `MyBookmarkSearchCond`,
- `OtherBookmarksSearchCond`
  를 모두 통합하는 역할을 한다.

`ultimateFindBookmarks` 는

- `findByCategory`
- `findFavoriteBookmarks`
- `findByTags`
- `findBookmarks`
  를 모두 통합하는 역할을 한다.

## 주석 규칙

- 클래스, 필드, 메서드에는 다음과 같은 주석을 단다.
```java
/**
* 북마크 (인터넷 즐겨찾기)
*/
```

- 함수 안 코드에는 다음과 같은 주석을 단다.
```java
/* 북마크 조회 */
```


