export function formatKorean(contentType: string) : string {
    if (contentType === 'WEBNOVEL') {
        return '웹소설';
    } else {
        return '웹툰';
    } 
}

export function formatUrl(contentType: string) : string {
    if (contentType === 'WEBNOVEL') {
        return 'webnovels';
    } else if (contentType === 'WEBTOON') {
        return 'webtoons';
    } else {
        return 'all';
    }
}