document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const playlistId = urlParams.get("id");

    if (!playlistId) {
        alert("Playlist não encontrada!");
        window.location.href = "menu.html";
        return;
    }

    carregarPlaylist(playlistId);
    configurarModalMusica(playlistId);

    document.getElementById("btnLogout").addEventListener("click", () => {
        localStorage.clear();
        window.location.href = "login.html";
    });
});

async function carregarPlaylist(playlistId) {

    const token = localStorage.getItem("token");

    const response = await fetch(`http://localhost:8080/playlists/${playlistId}`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    });

    if (!response.ok) {
        alert("Erro ao carregar playlist!");
        return;
    }

    const playlist = await response.json();

    document.getElementById("playlistTitulo").innerText = playlist.nome;

    const lista = document.getElementById("listaMusicas");
    lista.innerHTML = "";

    playlist.musicas.forEach((musica, index) => {

        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${musica.titulo}</td>
            <td>${musica.artista}</td>
            <td>${musica.duracao}</td>
            <td><button class="btn-delete" onclick="removerMusica(${playlist.id}, ${musica.id})">Deletar</button></td>
        `;

        lista.appendChild(tr);
    });
    document.getElementById("playlistCapa").src =
        playlist.foto ? playlist.foto : "images/default-cover.png";

    document.getElementById("playlistOwner").innerText =
        `Criada por ${playlist.usuario.nome} — ${playlist.musicas.length} músicas`;

}

async function removerMusica(playlistId, musicaId) {

    const token = localStorage.getItem("token");

    const confirmar = confirm("Deseja remover esta música?");
    if (!confirmar) return;

    const response = await fetch(
        `http://localhost:8080/playlists/${playlistId}/musicas/${musicaId}`,
        {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + token
            }
        }
    );

    await fetch(
        `http://localhost:8080/musicas/${musicaId}`,
        {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        }
    );

    if (!response.ok) {
        alert("Erro ao remover música!");
        return;
    }

    carregarPlaylist(playlistId);
}

function configurarModalMusica(playlistId) {

    const modal = document.getElementById("modalMusica");

    document.getElementById("btnAbrirModalMusica").addEventListener("click", () => {
        modal.classList.remove("hidden");
    });

    document.getElementById("btnCancelarMusica").addEventListener("click", () => {
        modal.classList.add("hidden");
    });

    document.getElementById("btnAdicionarMusica").addEventListener("click", async () => {

        const token = localStorage.getItem("token");

        const titulo = document.getElementById("musicaTitulo").value.trim();
        const artista = document.getElementById("musicaArtista").value.trim();
        const duracao = document.getElementById("musicaDuracao").value.trim();

        if (!titulo || !artista || !duracao) {
            alert("Preencha todos os campos!");
            return;
        }

        function converterDuracaoParaSegundos(duracao) {
            const [min, seg] = duracao.split(":").map(Number);
            return (min * 60) + seg;
        }

        const duracaoEmSegundos = converterDuracaoParaSegundos(duracao);

        const responseMusica = await fetch("http://localhost:8080/musicas", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                titulo,
                artista,
                duracaoEmSegundos
            })
        });

        if (!responseMusica.ok) {
            alert("Erro ao criar música!");
            return;
        }

        const novaMusica = await responseMusica.json();

        await fetch(
            `http://localhost:8080/playlists/${playlistId}/musicas/${novaMusica.id}`,
            {
                method: "POST",
                headers: { "Authorization": "Bearer " + token }
            }
        );

        modal.classList.add("hidden");

        carregarPlaylist(playlistId);
    });

}
