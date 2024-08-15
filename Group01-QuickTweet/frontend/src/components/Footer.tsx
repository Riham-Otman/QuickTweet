import { Container, Navbar, Row } from "react-bootstrap";

const Footer = () => {
  return (
    <Navbar
      bg='dark'
      data-bs-theme='dark'
      className='text-white'
      style={{ position: "relative", bottom: "-60vh", width: "100%" }}
    >
      <Container fluid className='align-items-center justify-content-center pt-2'>
        <Row>
          <p>© 2024 Dalhousie University | CSCI 3130 - Group 1</p>
        </Row>
      </Container>
    </Navbar>
  );
};

export default Footer;
