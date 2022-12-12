import express from 'express';
import cors from 'cors';
import https from 'https';
import fs from 'fs';
import { routes } from './routes/camera.routes';

const app = express();
app.use(cors())
app.use(express.json())
app.use(routes)

app.listen(process.env.PORT || 3001, () => {
    console.log('Server running')
})
/*const httpsServer = https.createServer({
    key: fs.readFileSync(''),
    cert: fs.readFileSync('')
}, app)

httpsServer.listen(443, () => {
    console.log('Server running')
})*/