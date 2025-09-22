
// Safe number type that handles undefined/null
export type SafeNumber = number | undefined | null;

// Safe string type that handles undefined/null
export type SafeString = string | undefined | null;

// Safe date type
export type SafeDate = string | Date | undefined | null;

// Extended interfaces with safe types
export interface SafeAutomobile extends Omit<import('./automobile').Automobile, 'dailyRate' | 'year'> {
    dailyRate: SafeNumber;
    year: SafeNumber;
}

export interface SafeRentalRequest extends Omit<import('./rental-request').RentalRequest, 'estimatedValue'> {
    estimatedValue: SafeNumber;
}

// Utility type for API responses that might have missing fields
export type ApiResponse<T> = {
    [K in keyof T]: T[K] extends number ? SafeNumber :
        T[K] extends string ? SafeString :
            T[K];
};

// Safe property access helpers
export interface SafePropertyAccess {
    safeGet<T>(obj: any, path: string, defaultValue?: T): T | undefined;
    safeGetNumber(obj: any, path: string, defaultValue?: number): number;
    safeGetString(obj: any, path: string, defaultValue?: string): string;
}

// Implementation of safe property access
export const SafeProps: SafePropertyAccess = {
    safeGet<T>(obj: any, path: string, defaultValue?: T): T | undefined {
        const keys = path.split('.');
        let result = obj;

        for (const key of keys) {
            if (result == null || result[key] === undefined) {
                return defaultValue;
            }
            result = result[key];
        }

        return result;
    },

    safeGetNumber(obj: any, path: string, defaultValue: number = 0): number {
        const value = this.safeGet(obj, path, defaultValue);
        return typeof value === 'number' && !isNaN(value) ? value : defaultValue;
    },

    safeGetString(obj: any, path: string, defaultValue: string = ''): string {
        const value = this.safeGet(obj, path, defaultValue);
        return typeof value === 'string' ? value : defaultValue;
    }
};

// Validation helpers
export const ValidationHelpers = {
    isValidNumber(value: any): value is number {
        return typeof value === 'number' && !isNaN(value) && isFinite(value);
    },

    isValidString(value: any): value is string {
        return typeof value === 'string' && value.trim().length > 0;
    },

    isValidDate(value: any): boolean {
        if (!value) return false;
        const date = new Date(value);
        return date instanceof Date && !isNaN(date.getTime());
    },

    sanitizeNumber(value: any, min?: number, max?: number): number | undefined {
        if (!this.isValidNumber(value)) return undefined;

        let result = value;
        if (min !== undefined && result < min) result = min;
        if (max !== undefined && result > max) result = max;

        return result;
    }
};