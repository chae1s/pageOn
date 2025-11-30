import dayjs from "dayjs";

export function formatDate(dateTimeString: string) : string {
    if (!dateTimeString) {
        return '';
    }

    return dayjs(dateTimeString).format('YYYY.MM.DD');

}

export function formatDateAndTime(dateTimeString: string) : string {
    if (!dateTimeString) {
        return '';
    }

    return dayjs(dateTimeString).format('YYYY.MM.DD HH:mm:ss');
}

export function formatNumber(num: number | string | undefined | null) : string {
    if (num === null || num === undefined || num === '') {
        return '0';
    }

    return Number(num).toLocaleString();
}