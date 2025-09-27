import { BaseApiService } from './base-api.service';
import { API_CONFIG } from '@/shared/config/api';
import type { UserCreateDTO, UserResponseDTO, LoginRequestDTO, LoginResponseDTO } from '@/shared/types/user';

export class AuthService extends BaseApiService {
    async register(userData: UserCreateDTO): Promise<UserResponseDTO> {
        return this.post<UserResponseDTO>('/auth/register', userData);
    }

    async login(credentials: LoginRequestDTO): Promise<LoginResponseDTO> {
        return this.post<LoginResponseDTO>('/auth/login', credentials);
    }

    async getCurrentUser(): Promise<UserResponseDTO> {
        const token = this.getToken();
        if (!token) {
            throw new Error('No authentication token found');
        }
        
        return this.request<UserResponseDTO>('/auth/me', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
    }

    private getToken(): string | null {
        return localStorage.getItem('auth_token');
    }

    setToken(token: string): void {
        localStorage.setItem('auth_token', token);
    }

    removeToken(): void {
        localStorage.removeItem('auth_token');
    }

    isAuthenticated(): boolean {
        return !!this.getToken();
    }
}
