import { useEffect, useState } from "react";
import { Button, Col, Container, Image, Row, Spinner } from "react-bootstrap";
import { checkUserInterest, countUsersWithInterest } from "../api/interestApi";
import { GroupPageProps } from "../types";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHouse } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router";

const GroupPage = ({ user, interest }: GroupPageProps) => {
  const [sharedInterestCount, setSharedInterestCount] = useState(0);
  const [userIsInGroup, setUserIsInGroup] = useState(false);
  const [loading, setLoading] = useState(true);
  const imageSrc = `../img/${interest}.png`;
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      if (interest) {
        try {
          if (user.username) {
            const userHasInterest = await checkUserInterest(user.username, interest);
            if (userHasInterest) {
              setUserIsInGroup(true);
            }
          }
          const count = await countUsersWithInterest(interest);
          if (count == undefined) {
            return;
          }
          setSharedInterestCount(count);
        } catch (error) {
          console.error("Error fetching data:", error);
        }
      }
      setLoading(false);
    };
    fetchData();
  }, []);

  let message = "";

  if (sharedInterestCount === 0) {
    message = `No users are in this group.`;
  } else {
    if (userIsInGroup) {
      if (sharedInterestCount === 1) {
        message = "You are in this group.";
      } else if (sharedInterestCount === 2) {
        message = "You and one other person are in this group.";
      } else if (sharedInterestCount > 2) {
        message = `You and ${sharedInterestCount - 1} other users are in this group.`;
      }
    } else {
      if (sharedInterestCount === 1) {
        message = "One user is in this group.";
      } else if (sharedInterestCount > 1) {
        message = `${sharedInterestCount} users are in this group.`;
      }
    }
  }

  if (loading) {
    return <Spinner animation='border' variant='primary' />;
  } else {
    return (
      <Container className='mt-5'>
        <Button variant='outline-secondary' onClick={() => navigate("/")} className='mb-3'>
          <FontAwesomeIcon icon={faHouse} />
        </Button>
        <Row className='justify-content-md-center' style={{ marginTop: "10%" }}>
          <Col md={6}>
            <div className='text-center'>
              <Image
                src={imageSrc}
                alt='Group Picture'
                roundedCircle
                thumbnail
                style={{ width: 250, height: 250 }}
              />
            </div>
            <h1 className='text-center' style={{ marginTop: "5%" }}>
              {interest}
            </h1>
            <div className='text-center'>
              <p>{message}</p>
            </div>
          </Col>
        </Row>
      </Container>
    );
  }
};

export default GroupPage;
