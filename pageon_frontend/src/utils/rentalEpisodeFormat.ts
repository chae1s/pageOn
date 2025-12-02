export function expirationDate(expiredAt: string) {
    const now = new Date();
    const endDate = new Date(expiredAt);

    const diff = endDate.getTime() - now.getTime();

    if (diff <= 0) {
        return "대여 만료";
    }

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff / (1000 * 60 * 60)) % 24);
    const minutes = Math.floor((diff / (1000 * 60)) % 60);

    if (days > 0) {
        return `${days}일 ${hours}시간 남음`
    }

    if (hours > 0) {
        return `${hours}시간 남음`;
    }

    return `${minutes}분 남음`;
}


export function expirationCheck(expiredAt: string) {
    const now = new Date();
    const endDate = new Date(expiredAt);

    const diff = endDate.getTime() - now.getTime();

    return diff <= 0;
}