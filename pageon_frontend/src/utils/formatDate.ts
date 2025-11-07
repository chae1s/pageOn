export function formatDate(dateTimeString: string) : string {
    if (!dateTimeString) {
        return '';
    }

    return dateTimeString.split('T')[0];

}