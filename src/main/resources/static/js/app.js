async function ask() {
  const message = document.getElementById('message').value;
  const includeTrace = document.getElementById('trace').checked;
  const res = await fetch('/api/v112/ask', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({message, includeTrace}) });
  document.getElementById('answer').textContent = JSON.stringify(await res.json(), null, 2);
}
async function ingestText() {
  const title = document.getElementById('title').value;
  const content = document.getElementById('content').value;
  const res = await fetch('/api/v112/rag/text', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({title, content, source:'ui'}) });
  document.getElementById('answer').textContent = JSON.stringify(await res.json(), null, 2);
}
async function ingestPdf() {
  const file = document.getElementById('pdf').files[0];
  const form = new FormData();
  form.append('file', file);
  const res = await fetch('/api/v112/rag/pdf', { method:'POST', body: form });
  document.getElementById('answer').textContent = JSON.stringify(await res.json(), null, 2);
}
async function searchRag() {
  const q = encodeURIComponent(document.getElementById('query').value);
  const res = await fetch('/api/v112/rag/search?q=' + q);
  document.getElementById('search').textContent = JSON.stringify(await res.json(), null, 2);
}
