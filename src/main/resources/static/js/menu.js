document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");

    if (!token) {
        alert("Você precisa fazer login primeiro!");
        window.location.href = "login.html";
        return;
    }

    const response = await fetch("http://localhost:8080/auth/validate", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    });

    if (!response.ok) {
        alert("Sessão expirada! Faça login novamente.");
        localStorage.clear();
        window.location.href = "login.html";
    }
});
