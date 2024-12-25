import sys
import sympy as sp
import json
import logging
from threading import Thread
from http.server import BaseHTTPRequestHandler, HTTPServer
import requests

# Configure logging
logging.basicConfig(level=logging.INFO, 
                    format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Eureka server URL and application information
EUREKA_SERVER_URL = "http://localhost:8761/eureka/apps"
APPLICATION_NAME = "PYTHON-CLIENT"
HOSTNAME = "192.168.100.22"  # Fixed IP address
INSTANCE_ID = f"PYTHON-CLIENT-{HOSTNAME}"
PORT = 8087
VIP_ADDRESS = APPLICATION_NAME
HEALTH_CHECK_URL = f"http://{HOSTNAME}:{PORT}/health"

# Eureka registration payload
payload = {
    "instance": {
        "hostName": HOSTNAME,
        "app": APPLICATION_NAME,
        "ipAddr": HOSTNAME,
        "status": "UP",
        "port": {"$": PORT, "@enabled": "true"},
        "securePort": {"$": 0, "@enabled": "false"},
        "vipAddress": VIP_ADDRESS,
        "homePageUrl": f"http://{HOSTNAME}:{PORT}",
        "healthCheckUrl": HEALTH_CHECK_URL,
        "statusPageUrl": f"http://{HOSTNAME}:{PORT}/info",
        "dataCenterInfo": {
            "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
            "name": "MyOwn"
        }
    }
}

def process_polynomial(data):
    """Traitement du polynôme (calcul des racines et factorisation)"""
    polynomial_str = data.get("polynomial", "")
    
    if not polynomial_str:
        return {"error": "No polynomial provided"}

    try:
        # Définir la variable symbolique x
        x = sp.symbols('x')
        
        # Convertir le polynôme en une expression SymPy
        polynomial = sp.sympify(polynomial_str)
        
        # Calculer les racines du polynôme
        roots = sp.solve(polynomial, x)
        
        # Effectuer la factorisation symbolique
        factorization = sp.factor(polynomial)
        
        # Convertir les racines en chaînes de caractères
        roots = [str(root) for root in roots]
        
        # Convertir la factorisation en chaîne
        factorization_str = str(factorization)
        
        return {
            "roots": roots,
            "factorization": factorization_str
        }
    except Exception as e:
        return {"error": str(e)}

def start_health_check_server():
    """Start a simple HTTP server for health check."""
    class HealthCheckHandler(BaseHTTPRequestHandler):
        def do_GET(self):
            if self.path == "/health":
                self.send_response(200)
                self.send_header("Content-type", "text/plain")
                self.end_headers()
                self.wfile.write(b"Healthy")
            else:
                self.send_response(404)
                self.end_headers()

    try:
        server = HTTPServer(('0.0.0.0', PORT), HealthCheckHandler)
        logger.info(f"Health check server running on http://{HOSTNAME}:{PORT}/health")
        server.serve_forever()
    except Exception as e:
        logger.error(f"Failed to start health check server: {e}")

def register_with_eureka():
    """Register the application with Eureka."""
    try:
        headers = {'Content-Type': 'application/json'}
        response = requests.post(f"{EUREKA_SERVER_URL}/{APPLICATION_NAME.upper()}", 
                                 json=payload, 
                                 headers=headers)
        if response.status_code == 204:
            logger.info(f"Successfully registered {APPLICATION_NAME} with Eureka.")
        else:
            logger.error(f"Failed to register with Eureka. Response: {response.status_code} - {response.text}")
    except Exception as e:
        logger.error(f"Eureka registration error: {e}")

def deregister_from_eureka():
    """Deregister the application from Eureka on shutdown."""
    try:
        response = requests.delete(f"{EUREKA_SERVER_URL}/{APPLICATION_NAME.upper()}/{INSTANCE_ID}")
        if response.status_code == 200:
            logger.info(f"Successfully deregistered {APPLICATION_NAME} from Eureka.")
        else:
            logger.error(f"Failed to deregister from Eureka. Response: {response.status_code} - {response.text}")
    except requests.RequestException as e:
        logger.error(f"Deregistration error: {e}")

def main():
    # Lire le JSON depuis l'entrée standard (stdin)
    input_data = sys.stdin.read()
    
    try:
        # Charger le JSON
        data = json.loads(input_data)
        
        # Process the polynomial
        result = process_polynomial(data)
        
        # Afficher la réponse JSON
        print(json.dumps(result))
    
    except Exception as e:
        # Si une erreur se produit, retourner une erreur JSON
        print(json.dumps({"error": str(e)}))
        sys.exit(1)

if __name__ == "__main__":
    try:
        # Start the health check server in a separate thread
        health_thread = Thread(target=start_health_check_server)
        health_thread.daemon = True  # Ensure the thread will close when main program exits
        health_thread.start()

        # Register with Eureka
        register_with_eureka()

        # Run the main function (process the polynomial)
        main()

    except KeyboardInterrupt:
        logger.info("Shutting down...")
        deregister_from_eureka()
        sys.exit(0)
