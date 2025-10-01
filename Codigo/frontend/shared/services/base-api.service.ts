import {API_CONFIG} from "@/shared/config/api";

export class BaseApiService {
    protected baseUrl: string;

    constructor() {
        this.baseUrl = API_CONFIG.BASE_URL;
    }

    protected async request<T>(
        endpoint: string,
        options: RequestInit = {}
    ): Promise<T> {
        const url = `${this.baseUrl}${endpoint}`;

        // Verificar se há token de autenticação
        const token = localStorage.getItem('auth_token');

        const defaultOptions: RequestInit = {
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` }),
                ...options.headers,
            },
        };

        try {
            const response = await fetch(url, { ...defaultOptions, ...options });

            // Se não for OK, extrair mensagem de erro do backend
            if (!response.ok) {
                let errorMessage = `HTTP error! status: ${response.status}`;
                let errorData: any = null;

                try {
                    // Tentar ler como JSON primeiro
                    const responseText = await response.text();

                    if (responseText) {
                        try {
                            errorData = JSON.parse(responseText);

                            // Tentar extrair mensagem de diferentes estruturas de erro
                            if (errorData.message) {
                                errorMessage = errorData.message;
                            } else if (errorData.error) {
                                errorMessage = errorData.error;
                            } else if (errorData.erro) {
                                errorMessage = errorData.erro;
                            } else if (errorData.errors && Array.isArray(errorData.errors)) {
                                errorMessage = errorData.errors.join(', ');
                            } else if (typeof errorData === 'string') {
                                errorMessage = errorData;
                            }
                        } catch (parseError) {
                            // Se não for JSON válido, usar o texto como mensagem
                            errorMessage = responseText;
                        }
                    }
                } catch (e) {
                    // Silenciar erro de leitura, usar mensagem padrão
                }

                // Criar objeto de erro customizado sem usar Error()
                // Isso evita que o Next.js mostre o erro no console
                const customError: any = {
                    message: errorMessage,
                    status: response.status,
                    response: {
                        data: errorData || {
                            message: errorMessage,
                            error: errorMessage
                        }
                    }
                };

                // Rejeitar a promise com o objeto customizado
                return Promise.reject(customError);
            }

            // Se for 204 ou sem conteúdo, retornar objeto vazio
            if (response.status === 204 || response.headers.get('content-length') === '0') {
                return {} as T;
            }

            return await response.json();
        } catch (error) {
            console.error(`API request failed for ${url}:`, error);
            throw error;
        }
    }

    protected async get<T>(endpoint: string): Promise<T> {
        return this.request<T>(endpoint, { method: 'GET' });
    }

    protected async post<T>(endpoint: string, data: unknown): Promise<T> {
        return this.request<T>(endpoint, {
            method: 'POST',
            body: JSON.stringify(data),
        });
    }

    protected async put<T>(endpoint: string, data: unknown): Promise<T> {
        return this.request<T>(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data),
        });
    }

    protected async patch<T>(endpoint: string, data: unknown): Promise<T> {
        return this.request<T>(endpoint, {
            method: 'PATCH',
            body: JSON.stringify(data),
        });
    }

    protected async delete<T>(endpoint: string): Promise<T> {
        return this.request<T>(endpoint, { method: 'DELETE' });
    }
}