import { useEffect, useState } from "react";
import Button from "react-bootstrap/Button";
import Card from "react-bootstrap/Card";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import { User, UserProfileProps } from "../types";
import { getAllUsers, getFriends } from "../api/userApi";

/**
 * `FriendRecommendations` is a React component that renders a list of recommended friends for the current user.
 *
 * This component fetches a list of all users and the current user's friends from the server. It then filters out the
 * current user's friends and the current user themselves from the list of all users to generate a list of recommended
 * friends. These recommendations are based on the premise that users not currently in the user's friend list could be
 * potential friends.
 *
 * The recommendations are displayed as a grid of cards, each representing a different user. Each card includes the user's
 * profile picture and username. The grid layout adapts to different screen sizes, ensuring a responsive design.
 *
 * @component
 * @param {UserProfileProps} props - The properties passed to the FriendRecommendations component.
 * @param {string} props.currentUsername - The username of the currently logged-in user, used to filter out the current user
 *                                         from the recommendations.
 * @returns A React element that displays a list of recommended friends in a responsive grid layout.
 */
const FriendRecommendations = ({ currentUsername }: UserProfileProps) => {
  const [allUsers, setAllUsers] = useState<User[]>([]);

  useEffect(() => {
    const fetchUsers = async () => {
      const users: User[] = await getAllUsers();
      const friends: User[] = await getFriends(currentUsername);
      const friendUsernames = friends.map(friend => friend.username);
      const uniqueUsers: User[] = users.reduce((result: User[], user: User) => {
        if (!friendUsernames.includes(user.username)) {
          result.push(user);
        }
        return result;
      }, []);
      setAllUsers(uniqueUsers);
    };

    fetchUsers();
  }, [currentUsername]);

  return (
    <div style={{ margin: "5vw" }}>
      <h1 style={{ marginBottom: "5%", fontSize: 30 }} className='display-5'>
        Recommended friends
      </h1>
      <Row xs={1} md={4} className='g-4'>
        {allUsers
          .filter(user => user.username != currentUsername)
          .slice(0, 3)
          .map(user => (
            <Col key={user.username}>
              <Card style={{ width: "18rem", margin: "auto" }}>
                <Card.Img
                  style={{
                    margin: "auto",
                    padding: "15%",
                    width: "20vh",
                    height: "20vh",
                  }}
                  variant='top'
                  src={user.photo ? user.photo : "././img/fox.png"}
                />
                <Card.Body style={{ margin: "auto" }}>
                  <Card.Title className='text-center'>{user.username}</Card.Title>
                  <Button variant='primary' href={`http://localhost:5173/profile/${user.username}`}>
                    Visit profile
                  </Button>
                </Card.Body>
              </Card>
            </Col>
          ))}
      </Row>
    </div>
  );
};

export default FriendRecommendations;
