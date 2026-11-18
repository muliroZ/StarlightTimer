const API_URL: string = import.meta.env.VITE_API_BASE_URL ?? "/api";

export async function makeRequest(endpoint: string) {
  const path = API_URL + endpoint;
  const res = await fetch(path);

  if (!res.ok) {
    let reason: string = `Falha na requisição: (HTTP ${res.status})`;

    try {
      const data = await res.json();
      reason = data.detail ?? data.title ?? reason;
    } catch (error) {
      console.error(error);
    }

    throw Error(reason);
  }

  return res;
}
