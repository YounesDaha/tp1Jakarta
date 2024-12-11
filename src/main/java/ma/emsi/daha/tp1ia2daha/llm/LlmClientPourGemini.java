package ma.emsi.daha.tp1ia2daha.llm;

// A modifier...

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

/**
 * Gère l'interface avec l'API de Gemini.
 * Son rôle est essentiellement de lancer une requête à chaque nouvelle
 * question qu'on veut envoyer à l'API.
 *
 * De portée dependent pour réinitialiser la conversation à chaque fois que
 * l'instance qui l'utilise est renouvelée.
 * Par exemple, si l'instance qui l'utilise est de portée View, la conversation est
 * réunitialisée à chaque fois que l'utilisateur quitte la page en cours.
 */
@Dependent
public class LlmClientPourGemini implements Serializable {
    // Clé pour l'API du LLM
    private final String key;
    // Client REST. Facilite les échanges avec une API REST.
    private Client clientRest;
    // Représente un endpoint de serveur REST
    private final WebTarget target;

    public LlmClientPourGemini() {
        // Récupère la clé secrète pour travailler avec l'API du LLM
        // (mise dans une variable d'environnement du système d'exploitation).
        this.key = System.getenv("GEMINI_KEY"); // Variable d'environnement à configurer
        if (this.key == null || this.key.isEmpty()) {
            throw new IllegalStateException("La clé API GEMINI_KEY n'est pas définie dans les variables d'environnement.");
        }

        // Initialise le client REST pour envoyer des requêtes vers les endpoints de l'API de Gemini
        this.clientRest = ClientBuilder.newClient();

        // Endpoint REST pour envoyer les requêtes à Gemini
        this.target = clientRest.target("https://ai.google.dev/gemini-api/docs/api-key?hl=fr"+this.key); // Remplacez par le bon URL de l'API
    }

    /**
     * Envoie une requête à l'API de Gemini.
     * @param requestEntity le corps de la requête (en JSON).
     * @return réponse REST de l'API (corps en JSON).
     */
    public Response envoyerRequete(Entity<String> requestEntity) {
        // Préparation de la requête avec le header Authorization et JSON comme type de contenu
        Invocation.Builder request = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Bearer " + this.key);

        // Envoie la requête POST au LLM
        return request.post(requestEntity);
    }

    /**
     * Ferme le client REST.
     * À appeler lors de la destruction de l'instance pour libérer les ressources.
     */
    public void closeClient() {
        if (this.clientRest != null) {
            this.clientRest.close();
        }
    }
}