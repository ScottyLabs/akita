import requests
from dotenv import load_dotenv, find_dotenv
import os

name = input("Enter the name of the application (PascalCase): ")


def get_all_flows(url, token):
    authorization_flow_url = f"{url}/api/v3/flows/instances/"
    response = requests.get(
        authorization_flow_url,
        headers={"Authorization": f"Bearer {token}"},
    )

    if response.ok:
        return response.json()["results"]
    else:
        print("Failed to get flows")
        print(response.json())
        exit()


def get_authorization_flow(flows):
    for flow in flows:
        if flow["slug"] == "default-provider-authorization-explicit-consent":
            return flow["pk"]


def get_invalidation_flow(flows):
    for flow in flows:
        if flow["slug"] == "default-provider-invalidation-flow":
            return flow["pk"]


def create_provider(url, token, name, authorization_flow, invalidation_flow):
    response = requests.post(
        f"{url}/api/v3/providers/oauth2/",
        headers={"Authorization": f"Bearer {token}"},
        json={
            "name": f"Prodvider for {name}",
            "authorization_flow": authorization_flow,
            "invalidation_flow": invalidation_flow,
            "redirect_uris": [],
        },
    )

    if response.ok:
        return response.json()
    else:
        print("Failed to create provider")
        print(response.json())
        exit()


def create_application(url, token, name, provider_id):
    response = requests.post(
        f"{url}/api/v3/core/applications/",
        headers={"Authorization": f"Bearer {token}"},
        json={
            "name": name,
            "slug": name.lower(),
            "provider": provider_id,
        },
    )

    if response.ok:
        print(f"Application {name} created successfully")
    else:
        print(f"Failed to create application {name}")
        print(response.json())
        exit()


for env in [".env.dev", ".env.staging", ".env.prod"]:
    print(f"Creating application for {env}")
    load_dotenv(find_dotenv(env), override=True)
    token = os.getenv("AUTHENTIK_API_TOKEN")
    url = os.getenv("AUTHENTIK_API_URL")

    flows = get_all_flows(url, token)
    authorization_flow = get_authorization_flow(flows)
    invalidation_flow = get_invalidation_flow(flows)
    provider = create_provider(url, token, name, authorization_flow, invalidation_flow)
    application = create_application(url, token, name, provider["pk"])

    # write to env file
    with open(f"{env}", "a") as f:
        client_id = provider["client_id"]
        f.write(f'OAUTH_{name.upper()}_ID="{client_id}"\n')

        client_secret = provider["client_secret"]
        f.write(f'OAUTH_{name.upper()}_SECRET="{client_secret}"\n')

    issuer = f"{url}/application/o/{name.lower()}/"
    f.write(f'{name.upper()}_ISSUER_URI="{issuer}"\n')

    private_url_suffix = os.getenv("PRIVATE_URL_SUFFIX")
    private_url = f"https://api-{name.lower()}-{private_url_suffix}"
    f.write(f'{name.upper()}_PRIVATE_URL="{private_url}"\n')

    public_host_suffix = os.getenv("PUBLIC_HOST_SUFFIX")
    public_host = f"api.{name.lower()}.{public_host_suffix}"
    f.write(f'{name.upper()}_PUBLIC_HOST="{public_host}"\n')
